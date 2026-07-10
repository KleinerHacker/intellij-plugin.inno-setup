/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.formatter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.PostFormatProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorFileType
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprBinary
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprCall
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprIndex
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprNode
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprOperatorCategory
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprParen
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprParser
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprSpan
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprTernary
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprUnary
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionHeader
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamPair
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes

/**
 * Performs the whole Inno Setup reformat with precise, PSI-guided document edits (the model itself is a no-op,
 * see [IsSectionFormattingModelBuilder]). Every rule is gated by its flag in [IsSectionCodeStyleSettings]:
 *
 * - **Rule 1** — exactly one blank line between section blocks.
 * - **Rule 2** — no spaces inside the section header brackets (`[Setup]`).
 * - **Rule 3.1** — one space around `=` in directive entries; **Rule 3.2** — no leading indentation.
 * - **Rule 4.1** — no space before / one after `;`; **Rule 4.2** — no leading indentation;
 *   **Rule 4.3** — no space before / one after `:`.
 * - **Rule 5** — one space around the arithmetic operators (`+ - * / %`) of preprocessor expressions.
 *
 * The `[Code]` section (Pascal Script) and everything after it are left untouched, since it must be the last
 * section in the script.
 */
class IsFormatterPostFormatProcessor : PostFormatProcessor {

    override fun processElement(source: PsiElement, settings: CodeStyleSettings): PsiElement {
        val file = source.containingFile ?: return source
        process(file, source.textRange, settings)
        return source
    }

    override fun processText(source: PsiFile, rangeToReformat: TextRange, settings: CodeStyleSettings): TextRange =
        process(source, rangeToReformat, settings)

    private fun process(file: PsiFile, range: TextRange, settings: CodeStyleSettings): TextRange {
        if (file.language != IsScriptLanguage) return range
        // Non-physical files (the Code Style live preview) have no document in PsiDocumentManager; fall back to
        // the view provider's document so the preview reflects the formatting too. Editing that document and
        // committing it is reflected back into the file's PSI text.
        val document = PsiDocumentManager.getInstance(file.project).getDocument(file)
            ?: file.viewProvider.document
            ?: return range
        val custom = settings.getCustomSettings(IsSectionCodeStyleSettings::class.java)
        val text = document.charsSequence

        // Everything from the [Code] header onward is free-form Pascal Script and must not be reformatted.
        val codeStart = codeSectionStart(file)

        val edits = mutableListOf<Edit>()
        if (custom.NO_SPACE_INSIDE_BRACKETS) collectHeaderBracketEdits(file, text, edits)
        if (custom.SPACE_AROUND_ASSIGN) collectAssignEdits(file, text, edits)
        if (custom.SPACE_AFTER_COLON) collectColonEdits(file, text, edits)
        if (custom.SPACE_AFTER_SEMICOLON) collectSemicolonEdits(file, text, edits)
        if (custom.TRIM_LEADING_KEY_SPACE) collectLeadingTrimEdits(file, text, edits)
        if (custom.SPACE_AROUND_PP_OPERATORS) collectPreprocessorOperatorEdits(file, text, edits)
        if (custom.BLANK_LINE_BETWEEN_SECTIONS) collectBlankLineEdits(file, document, edits)

        val applicable = edits
            .filter {
                it.start >= range.startOffset && it.end <= range.endOffset && it.end <= codeStart &&
                        it.replacement != it.original(text)
            }
            .sortedByDescending { it.start }

        var previousStart = Int.MAX_VALUE
        var delta = 0
        var changed = false
        for (edit in applicable) {
            if (edit.end > previousStart) continue // skip overlaps defensively
            document.replaceString(edit.start, edit.end, edit.replacement)
            delta += edit.replacement.length - (edit.end - edit.start)
            changed = true
            previousStart = edit.start
        }
        // Commit whenever any edit was applied — an edit can change content without changing overall length
        // (e.g. tightening brackets while adding spaces around '='), so this must not be gated on `delta`.
        if (changed) PsiDocumentManager.getInstance(file.project).commitDocument(document)
        return TextRange(range.startOffset, range.endOffset + delta)
    }

    /** Start offset of the `[Code]` section header, or [Int.MAX_VALUE] when there is none. */
    private fun codeSectionStart(file: PsiFile): Int =
        PsiTreeUtil.findChildrenOfType(file, IsSectionHeader::class.java)
            .filter { it.node.findChildByType(IsSectionTypes.TITLE)?.text?.trim().equals("Code", ignoreCase = true) }
            .minOfOrNull { it.textRange.startOffset } ?: Int.MAX_VALUE

    // ── Rule 2: header brackets ───────────────────────────────────────────────

    private fun collectHeaderBracketEdits(file: PsiFile, text: CharSequence, edits: MutableList<Edit>) {
        for (header in PsiTreeUtil.findChildrenOfType(file, IsSectionHeader::class.java)) {
            val lb = header.node.findChildByType(IsSectionTypes.LBRACKET)
            val title = header.node.findChildByType(IsSectionTypes.TITLE)
            val rb = header.node.findChildByType(IsSectionTypes.RBRACKET)
            if (lb != null && title != null) setSpaces(text, lb.textRange.endOffset, title.startOffset, 0, edits)
            if (title != null && rb != null) setSpaces(text, title.textRange.endOffset, rb.startOffset, 0, edits)
        }
    }

    // ── Rule 3.1: space around '=' ────────────────────────────────────────────

    private fun collectAssignEdits(file: PsiFile, text: CharSequence, edits: MutableList<Edit>) {
        for (entry in PsiTreeUtil.findChildrenOfType(file, IsSectionDirectiveEntry::class.java)) {
            val eq = entry.node.findChildByType(IsSectionTypes.EQ) ?: continue
            setSpacesBefore(text, eq.startOffset, 1, edits)
            setSpacesAfter(text, eq.textRange.endOffset, 1, edits)
        }
    }

    // ── Rule 4.3: ':' in parameter pairs ──────────────────────────────────────

    private fun collectColonEdits(file: PsiFile, text: CharSequence, edits: MutableList<Edit>) {
        for (pair in PsiTreeUtil.findChildrenOfType(file, IsSectionParamPair::class.java)) {
            val colon = pair.node.findChildByType(IsSectionTypes.COLON) ?: continue
            setSpacesBefore(text, colon.startOffset, 0, edits)
            setSpacesAfter(text, colon.textRange.endOffset, 1, edits)
        }
    }

    // ── Rule 4.1: ';' between parameter pairs ─────────────────────────────────

    private fun collectSemicolonEdits(file: PsiFile, text: CharSequence, edits: MutableList<Edit>) {
        for (entry in PsiTreeUtil.findChildrenOfType(file, IsSectionParameterEntry::class.java)) {
            var child = entry.node.firstChildNode
            while (child != null) {
                if (child.elementType == IsSectionTypes.SEMICOLON) {
                    setSpacesBefore(text, child.startOffset, 0, edits)
                    setSpacesAfter(text, child.textRange.endOffset, 1, edits)
                }
                child = child.treeNext
            }
        }
    }

    // ── Rule 5: preprocessor arithmetic operator spacing ──────────────────────

    private fun collectPreprocessorOperatorEdits(file: PsiFile, text: CharSequence, edits: MutableList<Edit>) {
        val lines = PsiTreeUtil.findChildrenOfType(file, IsSectionPreprocessorLine::class.java)
        if (lines.isEmpty()) return
        val psiFileFactory = PsiFileFactory.getInstance(file.project)

        for (line in lines) {
            // Parse the directive text as a standalone ISPP tree instead of going through injected PSI. The
            // injection is identity-mapped over the HASH_LINE range (see IsPreprocessorInjector), but it does
            // not resolve on non-physical files (the Code Style live preview) — parsing the raw text works in
            // both cases, and host offsets map directly (HASH_LINE start + offset within the directive text).
            val hashNode = line.node.findChildByType(IsSectionTypes.HASH_LINE) ?: continue
            val hashStart = hashNode.startOffset
            // ISPP has no registered <fileType>, so pass the file type explicitly (the language overload would
            // return null). eventSystemEnabled = false keeps this throwaway parse out of the PSI event system.
            val ppFile = psiFileFactory.createFileFromText(
                "a.ispp", IsPreprocessorFileType.INSTANCE, hashNode.text, 0L, false,
            )
            val directive = PsiTreeUtil.findChildOfType(ppFile, IsPreprocessorDirective::class.java)
                as? IsPreprocessorDirectiveEx ?: continue
            val base = (directive as IsPreprocessorDirective).textRange.startOffset

            for ((exprText, offsetInDirective) in expressionRegions(directive)) {
                val ast = IsPreprocessorExprParser.parse(exprText).ast
                val ops = mutableListOf<IsPreprocessorExprSpan>()
                collectArithmeticOperators(ast, ops)
                for (op in ops) {
                    // Exception: leave the increment/decrement operators `++` / `--` untouched. The engine
                    // has no `++`/`--` operator, so they tokenise as two adjacent `+`/`-`; a `+`/`-` that
                    // directly abuts an identical operator in the source must not get spaces around it.
                    if (isPartOfIncrementDecrement(exprText, op)) continue
                    val hostStart = hashStart + base + offsetInDirective + op.start
                    val hostEnd = hashStart + base + offsetInDirective + op.end
                    setSpacesBefore(text, hostStart, 1, edits)
                    setSpacesAfter(text, hostEnd, 1, edits)
                }
            }
        }
    }

    /** True when the single-char `+`/`-` operator [op] directly abuts an identical one (`++` / `--`). */
    private fun isPartOfIncrementDecrement(exprText: String, op: IsPreprocessorExprSpan): Boolean {
        if (op.end - op.start != 1) return false
        val ch = exprText[op.start]
        if (ch != '+' && ch != '-') return false
        val abutsBefore = op.start > 0 && exprText[op.start - 1] == ch
        val abutsAfter = op.end < exprText.length && exprText[op.end] == ch
        return abutsBefore || abutsAfter
    }

    /** All expression sub-ranges of a directive, as `(text, offsetWithinTheDirectiveLine)` pairs. */
    private fun expressionRegions(ex: IsPreprocessorDirectiveEx): List<Pair<String, Int>> {
        val regions = mutableListOf<Pair<String, Int>>()
        fun add(text: String?, offset: Int) {
            if (!text.isNullOrEmpty() && offset >= 0) regions += text to offset
        }
        add(ex.getDefineExpressionText(), ex.getDefineExpressionOffsetInDirective())
        add(ex.getConditionExpressionText(), ex.getConditionExpressionOffsetInDirective())
        add(ex.getForInitText(), ex.getForInitOffsetInDirective())
        add(ex.getForConditionText(), ex.getForConditionOffsetInDirective())
        add(ex.getForIncrementText(), ex.getForIncrementOffsetInDirective())
        add(ex.getForBodyText(), ex.getForBodyOffsetInDirective())
        add(ex.getArraySizeText(), ex.getArraySizeOffsetInDirective())
        add(ex.getDefineArrayIndexText(), ex.getDefineArrayIndexOffsetInDirective())
        return regions
    }

    private fun collectArithmeticOperators(node: IsPreprocessorExprNode, out: MutableList<IsPreprocessorExprSpan>) {
        when (node) {
            is IsPreprocessorExprBinary -> {
                if (node.operator.category == IsPreprocessorExprOperatorCategory.ARITHMETIC) out += node.opSpan
                collectArithmeticOperators(node.left, out)
                collectArithmeticOperators(node.right, out)
            }

            is IsPreprocessorExprUnary -> collectArithmeticOperators(node.operand, out)
            is IsPreprocessorExprParen -> collectArithmeticOperators(node.inner, out)
            is IsPreprocessorExprTernary -> {
                collectArithmeticOperators(node.condition, out)
                collectArithmeticOperators(node.whenTrue, out)
                collectArithmeticOperators(node.whenFalse, out)
            }

            is IsPreprocessorExprCall -> node.arguments.forEach { collectArithmeticOperators(it, out) }
            is IsPreprocessorExprIndex -> collectArithmeticOperators(node.index, out)
            else -> {}
        }
    }

    // ── Rules 3.2 / 4.2: no leading indentation before a key/parameter/header line ────────────────────

    private fun collectLeadingTrimEdits(file: PsiFile, text: CharSequence, edits: MutableList<Edit>) {
        val elements = buildList {
            addAll(PsiTreeUtil.findChildrenOfType(file, IsSectionHeader::class.java))
            addAll(PsiTreeUtil.findChildrenOfType(file, IsSectionDirectiveEntry::class.java))
            addAll(PsiTreeUtil.findChildrenOfType(file, IsSectionParameterEntry::class.java))
        }
        for (element in elements) {
            val ws = element.prevSibling as? PsiWhiteSpace ?: continue
            val lastBreak = ws.text.indexOfLast { it == '\n' || it == '\r' }
            if (lastBreak >= 0) {
                // The whitespace itself spans line break(s): keep the newlines, trim only the indent after the
                // last one. (A preceding entry may carry its trailing CRLF, so the break can live in this ws.)
                val indentStart = ws.textRange.startOffset + lastBreak + 1
                if (ws.textRange.endOffset > indentStart) edits += Edit(indentStart, ws.textRange.endOffset, "")
            } else {
                // Pure indentation: it is leading only if the preceding document character is a line break. The
                // sibling before it is not reliably a CRLF token — a previous entry ends with its own newline.
                val start = ws.textRange.startOffset
                val atLineStart = start == 0 || text[start - 1] == '\n' || text[start - 1] == '\r'
                if (atLineStart && ws.textLength > 0) edits += Edit(start, ws.textRange.endOffset, "")
            }
        }
    }

    // ── Rule 1: exactly one blank line between section blocks ─────────────────────────────────────────

    private fun collectBlankLineEdits(file: PsiFile, document: Document, edits: MutableList<Edit>) {
        val headers = PsiTreeUtil.findChildrenOfType(file, IsSectionHeader::class.java)
            .filter { it.node.treeParent?.elementType == IsSectionTypes.BLOCK }
            .sortedBy { it.textRange.startOffset }
        for (header in headers) {
            val headerLine = document.getLineNumber(header.textRange.startOffset)
            if (headerLine == 0) continue
            var prev = headerLine - 1
            while (prev >= 0 && isBlankLine(document, prev)) prev--

            if (prev < 0) {
                val end = document.getLineStartOffset(headerLine)
                if (end > 0) edits += Edit(0, end, "")
            } else {
                edits += Edit(document.getLineEndOffset(prev), document.getLineStartOffset(headerLine), "\n\n")
            }
        }
    }

    private fun isBlankLine(document: Document, line: Int): Boolean {
        val start = document.getLineStartOffset(line)
        val end = document.getLineEndOffset(line)
        return document.charsSequence.subSequence(start, end).isBlank()
    }

    // ── Whitespace helpers ────────────────────────────────────────────────────

    /** Normalizes the whitespace between [from] (exclusive) and [to] (a token start) to [target] spaces. */
    private fun setSpaces(text: CharSequence, from: Int, to: Int, target: Int, edits: MutableList<Edit>) {
        if (from > to) return
        val desired = " ".repeat(target)
        if (text.subSequence(from, to).toString() != desired) edits += Edit(from, to, desired)
    }

    /** Normalizes the whitespace directly before [boundary] to [target] spaces (skips a line start). */
    private fun setSpacesBefore(text: CharSequence, boundary: Int, target: Int, edits: MutableList<Edit>) {
        var start = boundary
        while (start > 0 && (text[start - 1] == ' ' || text[start - 1] == '\t')) start--
        if (start == 0 || text[start - 1] == '\n' || text[start - 1] == '\r') return // token starts the line
        setSpaces(text, start, boundary, target, edits)
    }

    /** Normalizes the whitespace directly after [boundary] to [target] spaces (skips a line end). */
    private fun setSpacesAfter(text: CharSequence, boundary: Int, target: Int, edits: MutableList<Edit>) {
        var end = boundary
        while (end < text.length && (text[end] == ' ' || text[end] == '\t')) end++
        if (end >= text.length || text[end] == '\n' || text[end] == '\r') return // token ends the line
        setSpaces(text, boundary, end, target, edits)
    }

    private data class Edit(val start: Int, val end: Int, val replacement: String) {
        fun original(text: CharSequence): String = text.subSequence(start, end).toString()
    }
}

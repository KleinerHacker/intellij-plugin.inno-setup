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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighter
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.Generated
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTypes

/**
 * Handles quote typing behavior for Inno Setup string literals.
 */
class IsQuoteHandler : TypedHandlerDelegate() {

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun beforeCharTyped(
        c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType
    ): Result {
        if ((file !is IsScriptFile && file !is IsPreprocessorFile) || c != '"') return Result.CONTINUE

        val offset = editor.caretModel.offset
        val text = editor.document.charsSequence
        val editorEx = editor as EditorEx

        // Skip over an existing closing quote instead of inserting a new one.
        // A QUOTE token is closing when the preceding token is QUOTE (empty string),
        // STRING_PART (content), or RBRACE (end of embedded constant).
        if (offset < text.length && text[offset] == '"') {
            val iter = editorEx.highlighter.createIterator(offset)
            if (!iter.atEnd() && iter.tokenType == IsSectionTypes.QUOTE && offset > 0) {
                val prev = editorEx.highlighter.createIterator(offset - 1)
                if (!prev.atEnd()) {
                    val pt = prev.tokenType
                    if (pt == IsSectionTypes.QUOTE || pt == IsSectionTypes.STRING_PART || pt == IsSectionTypes.RBRACE) {
                        editor.caretModel.moveToOffset(offset + 1)
                        return Result.STOP
                    }
                }
            }
        }

        // Don't auto-close when the cursor is already inside a string literal.
        if (offset > 0) {
            val iter = editorEx.highlighter.createIterator(offset - 1)
            if (!iter.atEnd()) {
                when (iter.tokenType) {
                    IsSectionTypes.STRING_PART, IsSectionTypes.RBRACE -> return Result.CONTINUE
                    IsSectionTypes.QUOTE ->
                        if (isOpeningQuote(editorEx.highlighter, iter)) return Result.CONTINUE
                }
            }
        }

        // Auto-insert the matching closing quote and place the cursor between them
        editor.document.insertString(offset, "\"\"")
        editor.caretModel.moveToOffset(offset + 1)

        return Result.STOP
    }

    /**
     * Whether the QUOTE token starting just before the caret is an *opening* quote
     * (caret inside a fresh, possibly empty, string) rather than a *closing* one. An
     * opening quote means auto-close must be suppressed; a closing quote lets the caller
     * fall through and start a new auto-closed string.
     *
     * Annotated [Generated] to exclude it from coverage: the `pos == 0` fallback is
     * unreachable because a QUOTE token is only lexed in the VALUE / IN_STRING states
     * (see `IsSectionLexer.flex`), which always have at least one preceding token, so the
     * token's start offset is never 0 here.
     */
    @Generated
    private fun isOpeningQuote(highlighter: EditorHighlighter, quote: HighlighterIterator): Boolean {
        val pos = quote.start
        if (pos == 0) return true

        val prev = highlighter.createIterator(pos - 1)
        if (prev.atEnd()) return false

        val pt = prev.tokenType

        return pt != IsSectionTypes.QUOTE && pt != IsSectionTypes.STRING_PART && pt != IsSectionTypes.RBRACE
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.script.language.feature.editor.section

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile

/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
class IsSectionTypedHandler : TypedHandlerDelegate() {

    /**
     * Auto-closes paired braces inside a preprocessor directive line (`#define meth(` → `(|)`, `#dim arr[` →
     * `[|]`, `#for {` → `{|}`).
     *
     * In the section host the whole `#…` line is a single opaque `HASH_LINE` token, so the platform's
     * token-driven paired-brace insertion cannot see the braces there — it works only via the (timing-sensitive)
     * injected ISPP fragment. This handler inserts the matching closing brace directly and is **idempotent**:
     * when the platform/injection already inserted it (the next character is already the closing brace) nothing
     * is added, so no double brace ever results. Insertion only happens in "safe" spots (end of line, before
     * whitespace or before another closing brace), mirroring the platform's own behaviour.
     */
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (!appliesToScript(project, file)) return Result.CONTINUE
        val close = when (c) {
            '(' -> ')'
            '[' -> ']'
            '{' -> '}'
            else -> return Result.CONTINUE
        }
        val offset = editor.caretModel.offset
        val chars = editor.document.charsSequence
        if (!isInPreprocessorLine(chars, offset)) return Result.CONTINUE

        val next = if (offset < chars.length) chars[offset] else null
        if (next == close) return Result.CONTINUE // already closed (by the platform/injection) — avoid doubling
        if (next != null && next != '\n' && next != '\r' && next != ' ' && next != '\t' &&
            next != ')' && next != ']' && next != '}'
        ) return Result.CONTINUE

        editor.document.insertString(offset, close.toString())
        editor.caretModel.moveToOffset(offset) // keep the caret between the braces
        return Result.STOP
    }

    /**
     * Whether the auto-close logic applies to [file]: either the script host itself, or — since 2026.2 — the
     * **injected ISPP fragment** of a `#…` directive line. In 2026.2 the caret inside a directive line sits
     * in the injected [IsPreprocessorFile] (with an injected editor window), so this delegate is invoked with
     * that injected file rather than the host [IsScriptFile]. Edits to the injected document still propagate
     * to the host, so the same insertion must run there; otherwise the closing brace is never inserted.
     */
    private fun appliesToScript(project: Project, file: PsiFile): Boolean =
        file is IsScriptFile ||
                (file is IsPreprocessorFile &&
                        InjectedLanguageManager.getInstance(project).getTopLevelFile(file) is IsScriptFile)

    /** Whether [offset] lies on a line whose first non-whitespace character is `#` (a preprocessor directive). */
    private fun isInPreprocessorLine(chars: CharSequence, offset: Int): Boolean {
        var i = offset - 1
        while (i >= 0 && chars[i] != '\n') i--
        var j = i + 1
        while (j < chars.length && (chars[j] == ' ' || chars[j] == '\t')) j++
        return j < chars.length && chars[j] == '#'
    }

    /**
     * Handles typed-character behavior for the current editor context.
     */
    override fun checkAutoPopup(
        charTyped: Char, project: Project, editor: Editor, file: PsiFile
    ): Result {
        if (file !is IsScriptFile) return Result.CONTINUE
        return when (charTyped) {
            '[', '{' -> {
                AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
                Result.STOP
            }

            '#' -> {
                val offset = editor.caretModel.offset
                val chars = editor.document.charsSequence
                // Inline preprocessor usage: {#…
                if (offset > 0 && chars[offset - 1] == '{') {
                    AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
                    Result.STOP
                } else if (isAtLineStart(chars, offset)) {
                    // Preprocessor directive line: # at the beginning of a (only whitespace-preceded) line
                    AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
                    Result.STOP
                } else {
                    Result.CONTINUE
                }
            }

            else -> Result.CONTINUE
        }
    }

    /**
     * Returns `true` if the given offset (the insertion point of the typed character) is at the start of a
     * line, i.e. only whitespace precedes it on the current line. Used to detect a preprocessor directive line.
     */
    private fun isAtLineStart(chars: CharSequence, offset: Int): Boolean {
        var i = offset - 1
        while (i >= 0) {
            val c = chars[i]
            if (c == '\n') return true
            if (c != ' ' && c != '\t') return false
            i--
        }
        return true
    }
}

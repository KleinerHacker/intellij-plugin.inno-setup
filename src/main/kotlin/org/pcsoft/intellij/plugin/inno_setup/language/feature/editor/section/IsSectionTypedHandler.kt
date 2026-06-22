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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile

/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
class IsSectionTypedHandler : TypedHandlerDelegate() {
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

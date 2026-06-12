/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
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
                if (offset > 0 && editor.document.charsSequence[offset - 1] == '{') {
                    AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
                    Result.STOP
                } else {
                    Result.CONTINUE
                }
            }

            else -> Result.CONTINUE
        }
    }
}

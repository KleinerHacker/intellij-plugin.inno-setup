package org.pcsoft.intellij.plugin.inno_setup.language.editor

import com.intellij.codeInsight.AutoPopupController
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile

class IssTypedHandler : TypedHandlerDelegate() {
    override fun checkAutoPopup(
        charTyped: Char, project: Project, editor: Editor, file: PsiFile
    ): Result {
        if (file !is IssFile) return Result.CONTINUE
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

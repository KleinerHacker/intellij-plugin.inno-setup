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

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile

/**
 * Intention action that extracts the selected lines of an `.iss` file into a new file and replaces them
 * with an `#include` of that file.
 *
 * The selection is expanded to full lines, a target file name is requested, the new file is created next
 * to the current script (overwriting an existing file only after confirmation), and the extracted lines
 * are replaced with `#include "<name>"`.
 */
class IsExtractIncludeIntentionAction : IntentionAction {

    /**
     * Returns the label shown in the intention popup.
     */
    override fun getText(): @IntentionName String = PluginBundle.message("intention.iss.include.extract.text")

    /**
     * Returns the common family name used to group Inno Setup intentions.
     */
    override fun getFamilyName(): @IntentionFamilyName String = "Inno Setup"

    /**
     * Available when there is a selection inside an `.iss` script file.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean =
        file is IsScriptFile && editor?.selectionModel?.hasSelection() == true

    /**
     * Extracts the selected full lines into a new file and replaces them with an `#include`.
     *
     * The file name dialog and overwrite confirmation are shown outside the write action; the actual
     * file creation and document edit run inside a single [WriteCommandAction].
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val hostFile = file as? IsScriptFile ?: return
        val selectionModel = editor?.selectionModel ?: return
        if (!selectionModel.hasSelection()) return

        val document = PsiDocumentManager.getInstance(project).getDocument(hostFile) ?: return
        val dir = hostFile.virtualFile?.parent ?: return

        // Expand the selection to cover full lines. When the selection ends exactly at a line start
        // (just after a line break), that trailing line is not part of the selection.
        val firstLine = document.getLineNumber(selectionModel.selectionStart)
        var lastLine = document.getLineNumber(selectionModel.selectionEnd)
        if (lastLine > firstLine && selectionModel.selectionEnd == document.getLineStartOffset(lastLine)) {
            lastLine--
        }
        val startOffset = document.getLineStartOffset(firstLine)
        val endOffset = document.getLineEndOffset(lastLine)
        val content = document.getText(com.intellij.openapi.util.TextRange(startOffset, endOffset))

        val fileName = Messages.showInputDialog(
            project,
            PluginBundle.message("intention.iss.include.extract.dialog.prompt"),
            PluginBundle.message("intention.iss.include.extract.dialog.title"),
            Messages.getQuestionIcon(),
            "include.ist",
            null,
        )?.trim()?.ifEmpty { null } ?: return

        val existing = dir.findChild(fileName)
        if (existing != null) {
            val choice = Messages.showYesNoDialog(
                project,
                PluginBundle.message("intention.iss.include.extract.overwrite.message", fileName),
                PluginBundle.message("intention.iss.include.extract.overwrite.title"),
                Messages.getWarningIcon(),
            )
            if (choice != Messages.YES) return
        }

        WriteCommandAction.runWriteCommandAction(project, getText(), null, {
            val target = existing ?: dir.createChildData(this, fileName)
            VfsUtil.saveText(target, content + "\n")
            document.replaceString(startOffset, endOffset, "#include \"$fileName\"")
            PsiDocumentManager.getInstance(project).commitDocument(document)
        }, hostFile)
    }

    /**
     * The dialogs require user interaction, so the action manages its own write action.
     */
    override fun startInWriteAction(): Boolean = false
}

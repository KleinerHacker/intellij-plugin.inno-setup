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

package org.pcsoft.intellij.plugin.inno_setup.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.VfsUtil
import org.jetbrains.annotations.VisibleForTesting
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.types.IslLanguageSpec

class IssCreateFileAction : DumbAwareAction(
    "Inno Setup Script",
    "Create new Inno Setup Script file",
    IssIcons.ScriptFile
) {
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val view = e.getData(LangDataKeys.IDE_VIEW)
        e.presentation.isEnabledAndVisible = view?.directories?.isNotEmpty() == true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val view = e.getData(LangDataKeys.IDE_VIEW) ?: return
        val dir = view.orChooseDirectory ?: return

        val dialog = IssCreateFileDialog(project)
        if (!dialog.showAndGet()) return

        WriteCommandAction.runWriteCommandAction(project, "Create Inno Setup Script", null, {
            val psiFile = dir.createFile("${dialog.fileName}.iss")
            VfsUtil.saveText(
                psiFile.virtualFile,
                buildTemplate(dialog.appName, dialog.appVersion, dialog.selectedLanguages)
            )
            FileEditorManager.getInstance(project).openFile(psiFile.virtualFile, true)
        })
    }

    @VisibleForTesting
    internal fun buildTemplate(
        appName: String,
        appVersion: String,
        languages: List<IslLanguageSpec>
    ) = buildString {
        appendLine("[Setup]")
        appendLine("AppName=$appName")
        appendLine("AppVersion=$appVersion")
        appendLine("DefaultDirName={autopf}\\$appName")
        appendLine("DefaultGroupName=$appName")
        appendLine()
        appendLine("[Languages]")
        languages.forEach { appendLine(it.toIssEntry()) }
        appendLine()
        append("[Files]")
    }
}

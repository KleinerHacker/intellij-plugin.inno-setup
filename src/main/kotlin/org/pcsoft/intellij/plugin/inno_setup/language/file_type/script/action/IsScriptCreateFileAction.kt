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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.VfsUtil
import org.jetbrains.annotations.VisibleForTesting
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.types.IsLanguageDataSpec

/**
 * Creates a new Inno Setup file from the IDE New File action. Name and description are localized via
 * [PluginBundle]; the icon comes from the `Iss.NewFile` action registration in `plugin.xml`.
 */
class IsScriptCreateFileAction : DumbAwareAction(
    PluginBundle.message("action.iss.new_file.text"),
    PluginBundle.message("action.iss.new_file.description"),
    null
) {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun update(e: AnActionEvent) {
        val view = e.getData(LangDataKeys.IDE_VIEW)
        e.presentation.isEnabledAndVisible = view?.directories?.isNotEmpty() == true
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val view = e.getData(LangDataKeys.IDE_VIEW) ?: return
        val dir = view.orChooseDirectory ?: return

        val dialog = IsScriptCreateFileDialog(project)
        if (!dialog.showAndGet()) return

        WriteCommandAction.runWriteCommandAction(project, PluginBundle.message("action.iss.new_file.command"), null, {
            dir.createFile("${dialog.fileName}.iss").let {
                VfsUtil.saveText(
                    it.virtualFile,
                    buildTemplate(dialog.appName, dialog.appVersion, dialog.selectedLanguages)
                )
                FileEditorManager.getInstance(project).openFile(it.virtualFile, true)
            }
        })
    }

    @VisibleForTesting
    internal fun buildTemplate(
        appName: String,
        appVersion: String,
        languages: List<IsLanguageDataSpec>
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

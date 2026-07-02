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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import javax.swing.Icon

/**
 * Creates a new, empty `.ist` (Inno Setup Template) file from the IDE New File action. A template is free
 * text, so the dialog only asks for the file name and no content template is generated.
 */
class IsTemplateCreateFileAction : DumbAwareAction(
    { PluginBundle.message("action.ist.new_file.text") },
    { PluginBundle.message("action.ist.new_file.description") },
    null as Icon?
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

        val dialog = IsTemplateCreateFileDialog(project)
        if (!dialog.showAndGet()) return

        WriteCommandAction.runWriteCommandAction(project, PluginBundle.message("action.ist.new_file.command"), null, {
            dir.createFile("${dialog.fileName}.ist").let {
                FileEditorManager.getInstance(project).openFile(it.virtualFile, true)
            }
        })
    }
}

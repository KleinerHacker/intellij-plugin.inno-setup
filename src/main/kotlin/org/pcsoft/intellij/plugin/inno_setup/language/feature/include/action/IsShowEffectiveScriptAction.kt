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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.effectiveScriptView
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.IsSectionAnnotatorHighlighting

/**
 * Context-menu action (Project View + editor popup) that opens the fully `#include`-resolved **effective
 * script** of a `.iss` file in a new, read-only editor tab. The view is created purely in memory (a
 * [LightVirtualFile] with the Inno Setup file type, so it gets the full `.iss` editor support); the text
 * pulled in through an `#include` is tinted with a translucent blue background.
 */
class IsShowEffectiveScriptAction : DumbAwareAction({ PluginBundle.message("action.iss.effective_script.text") }) {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val visible = e.project != null && singleScript(e) != null
        e.presentation.isVisible = visible
        e.presentation.isEnabled = visible
        if (visible) e.presentation.description = PluginBundle.message("action.iss.effective_script.description")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = singleScript(e) ?: return
        val host = PsiManager.getInstance(project).findFile(file) as? IsScriptFile ?: return
        openEffectiveScript(project, host)
    }

    private fun singleScript(e: AnActionEvent): VirtualFile? {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (files != null && files.size > 1) return null
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
        if (file.isDirectory || !file.extension.equals("iss", ignoreCase = true)) return null
        return file
    }

    companion object {
        /**
         * Builds the effective script of [host] and opens it in a read-only, in-memory editor tab, tinting the
         * `#include`-pulled ranges. Returns the created [LightVirtualFile] (or `null` when no editor opened).
         */
        fun openEffectiveScript(project: Project, host: IsScriptFile): LightVirtualFile? {
            val view = host.effectiveScriptView()

            val vFile = LightVirtualFile("effective_${host.name}", IsScriptFileType.INSTANCE, view.text)
            vFile.isWritable = false

            val editors = FileEditorManager.getInstance(project).openFile(vFile, true)
            val editor = editors.filterIsInstance<TextEditor>().firstOrNull()?.editor ?: return vFile

            val attributes = EditorColorsManager.getInstance().globalScheme
                .getAttributes(IsSectionAnnotatorHighlighting.INCLUDE_SEGMENT_BACKGROUND)
            view.includeRanges.forEach { range ->
                editor.markupModel.addRangeHighlighter(
                    range.startOffset,
                    range.endOffset.coerceAtMost(editor.document.textLength),
                    HighlighterLayer.ADDITIONAL_SYNTAX,
                    attributes,
                    HighlighterTargetArea.EXACT_RANGE,
                )
            }
            return vFile
        }
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.codeInsight.hints.*
import com.intellij.codeInsight.hints.presentation.InlayPresentation
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.IsIncludePaths
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import javax.swing.JPanel

/**
 * Renders the (resolved) content of a literal `#include "…"` directive as a block inlay hint below the
 * directive line. Each line of the included file becomes its own inlay line, so the preview keeps the file's
 * line breaks. Clicking any line jumps into the included file.
 *
 * No hint is shown when the include path cannot be resolved or the target file cannot be read (e.g. missing
 * file, directory, binary/unreadable content). The feature can be switched off on the Inno Setup *Editor*
 * settings page; it is on by default.
 */
@Suppress("UnstableApiUsage")
class IsIncludeContentInlayHintsProvider : InlayHintsProvider<NoSettings> {

    private companion object {
        /** Upper bound on rendered lines so a large include does not flood the editor. */
        const val MAX_LINES = 100
    }

    override val key: SettingsKey<NoSettings> = SettingsKey("inno.iss.include.content")
    override val name: String = PluginBundle.message("inlay.include_content.name")
    override val previewText: String = "#include \"common.iss\""

    override fun createSettings(): NoSettings = NoSettings()

    override fun createConfigurable(settings: NoSettings): ImmediateConfigurable =
        object : ImmediateConfigurable {
            override fun createComponent(listener: ChangeListener): JPanel = JPanel()
        }

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: NoSettings,
        sink: InlayHintsSink
    ): InlayHintsCollector = object : FactoryInlayHintsCollector(editor) {

        override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
            if (!IsSettingsService.getInstance().state.showIncludeContentInlayHints) return false
            if (element !is IsSectionPreprocessorLine) return true

            val directive = element.includeDirective() ?: return true
            val path = directive.getIncludePath() ?: return true

            val hostFile = element.containingFile as? IsScriptFile ?: return true
            val baseDir = hostFile.virtualFile?.parent ?: return true
            val target = IsIncludePaths.resolve(baseDir, path) ?: return true
            if (target.isDirectory) return true

            val lines = readLines(target) ?: return true
            if (lines.isEmpty()) return true

            addContentInlays(element.textRange.startOffset, lines, target, sink)
            return true
        }

        /** The injected literal `#include` directive of this host line, or `null`. */
        private fun IsSectionPreprocessorLine.includeDirective(): IsPreprocessorDirectiveEx? {
            var found: IsPreprocessorDirectiveEx? = null
            InjectedLanguageManager.getInstance(project).enumerate(this) { injected, _ ->
                if (found == null && injected is IsPreprocessorFile) {
                    val directive = PsiTreeUtil.findChildOfType(injected, IsPreprocessorDirective::class.java)
                            as? IsPreprocessorDirectiveEx
                    if (directive != null && directive.isInclude()) found = directive
                }
            }
            return found
        }

        /** Reads the file content as editor lines, or `null` when it cannot be read. */
        private fun readLines(target: VirtualFile): List<String>? =
            try {
                VfsUtilCore.loadText(target)
                    .split('\n')
                    .map { it.trimEnd('\r') }
                    .let { if (it.size > MAX_LINES) it.take(MAX_LINES) + "…" else it }
            } catch (_: Exception) {
                null
            }

        /** Adds one block inlay per content line below [offset]; clicking opens [target]. */
        private fun addContentInlays(offset: Int, lines: List<String>, target: VirtualFile, sink: InlayHintsSink) {
            lines.forEachIndexed { index, line ->
                // Shown below the directive line; higher priority sticks closer to it, so line 0 ends up on top.
                val priority = lines.size - index
                sink.addBlockElement(offset, true, false, priority, linePresentation(line, target))
            }
        }

        private fun linePresentation(line: String, target: VirtualFile): InlayPresentation {
            // Render even blank lines with a marker so the included file's structure stays visible.
            val text = factory.smallText(if (line.isEmpty()) "▏" else "▏ $line")
            return factory.referenceOnHover(text) { _, _ ->
                val project = editor.project ?: return@referenceOnHover
                OpenFileDescriptor(project, target).navigate(true)
            }
        }
    }
}

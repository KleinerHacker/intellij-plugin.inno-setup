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
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.IsIncludePaths
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionPreprocessorLine

/**
 * Intention action that replaces an `#include "file"` line with the verbatim content of the included
 * file (one level — nested `#include` lines in the inserted text are kept as-is).
 *
 * Offered when the caret sits on a `#include` line whose single-literal path resolves to an existing
 * file (relative to the including script's directory).
 */
class IsIncludeInlineIntentionAction : IntentionAction {

    /**
     * Returns the label shown in the intention popup.
     */
    override fun getText(): @IntentionName String = PluginBundle.message("intention.iss.include.inline.text")

    /**
     * Returns the common family name used to group Inno Setup intentions.
     */
    override fun getFamilyName(): @IntentionFamilyName String = "Inno Setup"

    /**
     * Available when the caret is on an `#include` line that resolves to an existing file.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean =
        resolveTarget(project, editor, file) != null

    /**
     * Replaces the whole `#include` line with the included file's text.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val (line, target) = resolveTarget(project, editor, file) ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(line.containingFile) ?: return

        val content = VfsUtilCore.loadText(target).removeSuffix("\n").removeSuffix("\r")
        val range = line.textRange
        document.replaceString(range.startOffset, range.endOffset, content)
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    /**
     * Indicates that the document modification is performed inside IntelliJ's write action.
     */
    override fun startInWriteAction(): Boolean = true

    /**
     * Resolves the `#include` line at the caret together with its existing target file, or `null`.
     */
    private fun resolveTarget(project: Project, editor: Editor?, file: PsiFile?): Pair<IsSectionPreprocessorLine, VirtualFile>? {
        if (file == null) return null
        // The caret may sit inside the injected ISPP fragment of a `#` line, so recover the host script.
        val hostFile = file as? IsScriptFile
            ?: InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as? IsScriptFile
            ?: return null
        val offset = editor?.caretModel?.offset ?: return null
        val elementAt = hostFile.findElementAt(offset) ?: return null
        val line = PsiTreeUtil.getParentOfType(elementAt, IsSectionPreprocessorLine::class.java, false)
            ?: return null

        val directive = injectedDirective(project, line) ?: return null
        if (!directive.isInclude()) return null
        val path = directive.getIncludePath() ?: return null
        val baseDir = hostFile.virtualFile?.parent ?: return null
        val target = IsIncludePaths.resolve(baseDir, path)?.takeIf { it.isValid && !it.isDirectory } ?: return null
        return line to target
    }

    /**
     * The ISPP directive injected into [line], or `null` when none is present.
     */
    private fun injectedDirective(project: Project, line: IsSectionPreprocessorLine): IsPreprocessorDirective? {
        var result: IsPreprocessorDirective? = null
        InjectedLanguageManager.getInstance(project).enumerate(line) { injectedPsi, _ ->
            if (result == null) {
                result = PsiTreeUtil.findChildOfType(injectedPsi, IsPreprocessorDirective::class.java)
            }
        }
        return result
    }
}

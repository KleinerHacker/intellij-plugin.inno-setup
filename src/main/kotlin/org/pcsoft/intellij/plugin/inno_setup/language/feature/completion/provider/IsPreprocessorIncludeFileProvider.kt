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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorQuotedString
import java.io.File

/**
 * Inside an `#include "…"` string, suggests every `*.iss` file of the project by its name. On insert the
 * path **relative to the directory of the edited script** is written (so the include works from that file).
 */
object IsPreprocessorIncludeFileProvider : CompletionProvider<CompletionParameters>() {

    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        // Only on the value of an #include directive (typically inside its "…" string). #ifexist/#ifnexist
        // test for *any* file on disk, not just script/template include targets, so the .iss/.isl list would
        // be misleading there — no file completion is offered for them.
        val directive = PsiTreeUtil.getParentOfType(position, IsPreprocessorDirectiveEx::class.java) ?: return
        if (!directive.isInclude()) return
        val inString = PsiTreeUtil.getParentOfType(position, IsPreprocessorQuotedString::class.java) != null
        val afterKeyword = (directive as IsPreprocessorDirective).value != null
        if (!inString && !afterKeyword) return

        val project = position.project
        // Resolve the host script from the original (physical) file — the completion copy's host carries a
        // light virtual file without a parent directory.
        val injMgr = InjectedLanguageManager.getInstance(project)
        val hostFile = injMgr.getTopLevelFile(parameters.originalFile) as? IsScriptFile ?: return
        val hostDir = hostFile.virtualFile?.parent?.path ?: return

        val scope = GlobalSearchScope.projectScope(project)
        // Both real scripts (.iss) and free-text template fragments (.ist) are valid include targets.
        (FileTypeIndex.getFiles(IsScriptFileType.INSTANCE, scope) +
                FileTypeIndex.getFiles(IsTemplateFileType.INSTANCE, scope))
            .filter { it != hostFile.virtualFile }
            .forEach { vf ->
                val relative = relativePath(hostDir, vf)
                result.addElement(
                    LookupElementBuilder.create(vf.name)
                        .withIcon(IsIcons.ScriptFile)
                        .withTypeText(relative)
                        .withInsertHandler { ctx, _ ->
                            ctx.document.replaceString(ctx.startOffset, ctx.tailOffset, relative)
                            ctx.commitDocument()
                        }
                )
            }
    }

    /** The path of [target] relative to [hostDir], using `/` separators. */
    private fun relativePath(hostDir: String, target: VirtualFile): String =
        runCatching {
            File(hostDir).toPath().relativize(File(target.path).toPath()).toString().replace('\\', '/')
        }.getOrDefault(target.name)
}

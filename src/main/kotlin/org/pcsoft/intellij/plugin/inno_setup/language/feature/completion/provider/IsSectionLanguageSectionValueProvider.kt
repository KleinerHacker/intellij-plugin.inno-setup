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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.feature.IsLanguageIconHelper
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.IsLanguageFileType
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.*
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionQuotedString
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
object IsSectionLanguageSectionValueProvider : CompletionProvider<CompletionParameters>() {
    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection) return
        val paramValue = PsiTreeUtil.getParentOfType(position, IsSectionParamValue::class.java) ?: return
        val pair = paramValue.containingParamPair ?: return
        val section = pair.containingSection ?: return
        if (!section.nameText.equals("Languages", ignoreCase = true)) return

        // STRING_PART tokens inside IsSectionQuotedString don't get automatic prefix computation,
        // so strip the dummy identifier manually to set the correct prefix.
        val adjustedResult = if (PsiTreeUtil.getParentOfType(position, IsSectionQuotedString::class.java) != null) {
            val tokenText = position.text
            val dummyIdx = tokenText.indexOf(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)
            val typed = if (dummyIdx >= 0) tokenText.substring(0, dummyIdx) else tokenText
            result.withPrefixMatcher(typed)
        } else result

        val builtin = service<IsLanguageDataService>().builtinLanguages
        val key = pair.keyText()
        when {
            key.equals("MessagesFile", ignoreCase = true) -> {
                val builtinInsertTexts = mutableSetOf<String>()
                builtin.forEach { lang ->
                    val messagesFile = lang.messagesFile ?: return@forEach
                    builtinInsertTexts += messagesFile.replace('\\', '/').lowercase()
                    adjustedResult.addElement(
                        PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(messagesFile)
                                .withTypeText(lang.displayName)
                                .withIcon(lang.icon),
                            10.0
                        )
                    )
                }
                // Additionally offer project-local .isl files as relative paths.
                val project = parameters.position.project
                val scriptVf = parameters.originalFile.virtualFile
                val scriptDir = scriptVf?.parent
                val islFiles = FileTypeIndex.getFiles(
                    IsLanguageFileType.INSTANCE,
                    GlobalSearchScope.projectScope(project)
                )
                islFiles.forEach { vf ->
                    val relPath = if (scriptDir != null) relativePath(scriptDir, vf) else vf.name
                    if (relPath.replace('\\', '/').lowercase() in builtinInsertTexts) return@forEach
                    val (typeText, icon) = islTypeTextAndIcon(vf, project)
                    adjustedResult.addElement(
                        PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(relPath)
                                .withTypeText(typeText)
                                .withIcon(icon),
                            9.0
                        )
                    )
                }
            }
        }
    }

    private fun relativePath(scriptDir: VirtualFile, target: VirtualFile): String {
        // Build a relative path from scriptDir to target using VFS paths.
        val scriptPath = scriptDir.path
        val targetPath = target.path
        return if (targetPath.startsWith("$scriptPath/")) {
            targetPath.removePrefix("$scriptPath/").replace('/', '\\')
        } else {
            target.name
        }
    }

    private fun islTypeTextAndIcon(
        vf: VirtualFile,
        project: com.intellij.openapi.project.Project
    ): Pair<String, javax.swing.Icon> {
        val psiFile = PsiManager.getInstance(project).findFile(vf) as? IsScriptFile
        val langOptions = psiFile?.findSection("LangOptions")
        val langName = langOptions?.directiveEntryList
            ?.firstOrNull { it.keyText().equals("LanguageName", ignoreCase = true) }
            ?.valueText?.trim()?.removeSurrounding("\"")
        val langId = langOptions?.directiveEntryList
            ?.firstOrNull { it.keyText().equals("LanguageID", ignoreCase = true) }
            ?.valueText?.trim()
        val typeText = when {
            langName != null && langId != null -> "$langName ($langId)"
            langName != null -> langName
            else -> vf.name
        }
        val icon = IsLanguageIconHelper.flagIconForIslFile(psiFile)
        return typeText to icon
    }
}

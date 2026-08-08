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
import com.intellij.openapi.components.service
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.isppDirectivesWithHostOffset
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.lang.IsLanguageFile

/**
 * Completion for the name position of a `#define`/`#undef`:
 *  - the optional scope/visibility keywords (`public`/`protected`/`private`) — offered while none is
 *    present yet, presented like keywords;
 *  - for `#undef` only, the names of the `#define`s declared on an earlier line (declaration-order aware),
 *    so an `#undef` can be completed against an existing macro.
 *
 * A `#define` name is a *new* declaration, so existing names are not offered there.
 */
object IsPreprocessorDefineNameProvider : CompletionProvider<CompletionParameters>() {

    // Caret sits in the name position of #define/#undef/#ifdef/#ifndef: the directive keyword, optionally a
    // scope keyword (only #define/#undef carry one), then the (partial) word being typed with no following
    // whitespace.
    private val NAME_PREFIX = Regex(
        "^#\\s*(define|undef|ifdef|ifndef|dim|redim)\\s+(?:(public|protected|private)\\s+)?([A-Za-z0-9_.\\-]*)$",
        RegexOption.IGNORE_CASE,
    )

    // Directives whose name argument refers to an existing #define (so earlier names are offered).
    private val NAME_REFERENCE_DIRECTIVES = setOf("undef", "ifdef", "ifndef")

    // Directives that carry a scope/visibility keyword.
    private val SCOPE_DIRECTIVES = setOf("define", "undef", "dim", "redim")

    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val offset = params.offset
        val doc = params.editor.document
        val lineStart = doc.getLineStartOffset(doc.getLineNumber(offset))
        val linePrefix = doc.charsSequence.subSequence(lineStart, offset).toString()
        val match = NAME_PREFIX.matchEntire(linePrefix) ?: return

        val directive = match.groupValues[1].lowercase()
        val scopePresent = match.groupValues[2].isNotEmpty()
        val typed = match.groupValues[3]
        val adjusted = result.withPrefixMatcher(typed)

        val position = params.position
        val hostFile = InjectedLanguageManager.getInstance(position.project)
            .getTopLevelFile(position.containingFile)
        if (hostFile is IsLanguageFile) return

        // Scope/visibility keywords — only for #define/#undef/#dim/#redim and only when none chosen yet.
        if (!scopePresent && directive in SCOPE_DIRECTIVES) {
            service<IsPreprocessorService>().spec.visibilityKeywords.forEach { keyword ->
                adjusted.addElement(
                    LookupElementBuilder.create(keyword.name)
                        .withTypeText("ISPP keyword")
                        .withIcon(IsIcons.Constant)
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, " ")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                        }
                )
            }
        }

        // For #undef/#ifdef/#ifndef: the names of earlier #defines (a #define name is a new declaration →
        // none offered).
        if (directive in NAME_REFERENCE_DIRECTIVES && hostFile is IsScriptFile) {
            val host = InjectedLanguageManager.getInstance(position.project).getInjectionHost(position)
            val lineOffset = host?.textRange?.startOffset ?: Int.MAX_VALUE
            hostFile.isppDirectivesWithHostOffset
                .filter { (d, off) ->
                    off < lineOffset && (d as? IsPreprocessorDirectiveEx)?.isDefine() == true && !d.isArrayElementDefine()
                }
                .mapNotNull { it.first as? IsPreprocessorDirectiveEx }
                .filter { !it.getDefineName().isNullOrEmpty() }
                .distinctBy { it.getDefineName() }
                .forEach { dex ->
                    adjusted.addElement(
                        LookupElementBuilder.create(dex.getDefineName()!!)
                            .withTypeText("define")
                            .withIcon(IsIcons.Variable)
                    )
                }
        }

        // For #define: the names of earlier #dim arrays — a `#define Name[i]` assigns an array element, so the
        // array name is completable in the name position (inserted with `[]` and the caret placed inside).
        if (directive == "define" && hostFile is IsScriptFile) {
            val host = InjectedLanguageManager.getInstance(position.project).getInjectionHost(position)
            val lineOffset = host?.textRange?.startOffset ?: Int.MAX_VALUE
            hostFile.isppDirectivesWithHostOffset
                .filter { (d, off) -> off < lineOffset && (d as? IsPreprocessorDirectiveEx)?.isDim() == true }
                .mapNotNull { it.first as? IsPreprocessorDirectiveEx }
                .filter { !it.getArrayName().isNullOrEmpty() }
                .distinctBy { it.getArrayName() }
                .forEach { dex ->
                    adjusted.addElement(
                        LookupElementBuilder.create(dex.getArrayName()!!)
                            .withTailText("[]", true)
                            .withTypeText("array")
                            .withIcon(IsIcons.Variable)
                            .withInsertHandler { ctx, _ ->
                                ctx.document.insertString(ctx.tailOffset, "[]")
                                ctx.editor.caretModel.moveToOffset(ctx.tailOffset - 1)
                            }
                    )
                }
        }

        // For #redim: the names of earlier #dim arrays (declaration-order aware).
        if (directive == "redim" && hostFile is IsScriptFile) {
            val host = InjectedLanguageManager.getInstance(position.project).getInjectionHost(position)
            val lineOffset = host?.textRange?.startOffset ?: Int.MAX_VALUE
            hostFile.isppDirectivesWithHostOffset
                .filter { (d, off) -> off < lineOffset && (d as? IsPreprocessorDirectiveEx)?.isDim() == true }
                .mapNotNull { it.first as? IsPreprocessorDirectiveEx }
                .filter { !it.getArrayName().isNullOrEmpty() }
                .distinctBy { it.getArrayName() }
                .forEach { dex ->
                    adjusted.addElement(
                        LookupElementBuilder.create(dex.getArrayName()!!)
                            .withTypeText("array")
                            .withIcon(IsIcons.Variable)
                    )
                }
        }
    }
}

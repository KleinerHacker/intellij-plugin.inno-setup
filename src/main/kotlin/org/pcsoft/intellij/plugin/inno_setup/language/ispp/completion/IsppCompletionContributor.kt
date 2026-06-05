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

package org.pcsoft.intellij.plugin.inno_setup.language.ispp.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.isppDirectivesWithHostOffset
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes
import org.pcsoft.intellij.plugin.inno_setup.services.IssIsppService

class IsppCompletionContributor : CompletionContributor() {
    init {
        // Directive keyword after #
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsppTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IsppTypes.HASH))
                .withParent(IsppDirective::class.java),
            IsppDirectiveKeywordProvider
        )
        // Names of preceding #defines inside a #define expression
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(IsppLanguage),
            IsppDefineExpressionProvider
        )
    }
}

private object IsppDirectiveKeywordProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        service<IssIsppService>().spec.directives
            .distinctBy { it.name }
            .forEach { dir ->
                result.addElement(
                    LookupElementBuilder.create(dir.name)
                        .withTypeText("ISPP")
                        .withTailText("  ${dir.syntax}", true)
                        .withIcon(IssIcons.Constant)
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, " ")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                        }
                )
            }
    }
}

/**
 * Inside a `#define` expression, suggest the names of all `#define`s declared on an earlier line.
 * Declaration order is enforced via host offsets, so neither the current define nor later ones appear.
 */
private object IsppDefineExpressionProvider : CompletionProvider<CompletionParameters>() {
    // There is already a complete name (with optional parameter list) followed by whitespace,
    // i.e. the caret sits in the expression part.
    private val EXPR_PREFIX = Regex("^#\\s*define\\s+[A-Za-z0-9_.\\-]+(?:\\([^)]*\\))?\\s+.*$")
    private val WORD_TAIL = Regex("[A-Za-z0-9_.\\-]*$")

    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val offset = params.offset
        val doc = params.editor.document
        val lineStart = doc.getLineStartOffset(doc.getLineNumber(offset))
        val linePrefix = doc.charsSequence.subSequence(lineStart, offset).toString()
        if (!EXPR_PREFIX.matches(linePrefix)) return

        val position = params.position
        val injMgr = InjectedLanguageManager.getInstance(position.project)
        val hostFile = injMgr.getTopLevelFile(position.containingFile) as? IssFile ?: return
        val host = injMgr.getInjectionHost(position) ?: return
        val lineOffset = host.textRange.startOffset

        val precedingNames = hostFile.isppDirectivesWithHostOffset()
            .filter { (d, off) -> off < lineOffset && (d as? IsppDirectiveEx)?.isDefine() == true }
            .mapNotNull { (it.first as? IsppDirectiveEx)?.getDefineName()?.ifEmpty { null } }
            .distinct()

        val typed = WORD_TAIL.find(linePrefix)?.value ?: ""
        val adjusted = result.withPrefixMatcher(typed)
        precedingNames.forEach { name ->
            adjusted.addElement(
                LookupElementBuilder.create(name)
                    .withTypeText("define")
                    .withIcon(IssIcons.Variable)
            )
        }
    }
}

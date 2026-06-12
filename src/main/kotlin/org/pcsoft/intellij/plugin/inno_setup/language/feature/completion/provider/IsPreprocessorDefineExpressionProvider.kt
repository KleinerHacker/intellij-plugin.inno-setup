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

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.isppDirectivesWithHostOffset
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirectiveEx

/**
 * Inside a `#define` expression, suggest the names of all `#define`s declared on an earlier line.
 * Declaration order is enforced via host offsets, so neither the current define nor later ones appear.
 */
object IsPreprocessorDefineExpressionProvider : CompletionProvider<CompletionParameters>() {
    // There is already a complete name (with optional parameter list) followed by whitespace,
    // i.e. the caret sits in the expression part.
    private val EXPR_PREFIX = Regex("^#\\s*define\\s+[A-Za-z0-9_.\\-]+(?:\\([^)]*\\))?\\s+.*$")
    private val WORD_TAIL = Regex("[A-Za-z0-9_.\\-]*$")

    /**
     * Adds lookup elements for the current completion request.
     */
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
        val hostFile = injMgr.getTopLevelFile(position.containingFile) as? IsScriptFile ?: return
        val host = injMgr.getInjectionHost(position) ?: return
        val lineOffset = host.textRange.startOffset

        val precedingNames = hostFile.isppDirectivesWithHostOffset
            .filter { (d, off) -> off < lineOffset && (d as? IsPreprocessorDirectiveEx)?.isDefine() == true }
            .mapNotNull { (it.first as? IsPreprocessorDirectiveEx)?.getDefineName()?.ifEmpty { null } }
            .distinct()

        val typed = WORD_TAIL.find(linePrefix)?.value ?: ""
        val adjusted = result.withPrefixMatcher(typed)
        precedingNames.forEach { name ->
            adjusted.addElement(
                LookupElementBuilder.create(name)
                    .withTypeText("define")
                    .withIcon(IsIcons.Variable)
            )
        }
    }
}

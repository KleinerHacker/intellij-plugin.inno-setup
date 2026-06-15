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
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
object IsSectionPreprocessorVariableAfterHashProvider : CompletionProvider<CompletionParameters>() {
    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val offset = parameters.offset
        val chars = parameters.editor.document.charsSequence
        val lookBack = minOf(offset, 100)
        val prefix = chars.subSequence(offset - lookBack, offset).toString()
        val braceIdx = prefix.lastIndexOf('{')
        if (braceIdx < 0) return
        val afterBrace = prefix.substring(braceIdx + 1)
        if (!afterBrace.startsWith('#')) return
        val typedName = afterBrace.substring(1)

        val file = parameters.originalFile as? IsScriptFile ?: return
        val adjusted = result.withPrefixMatcher(typedName)

        // The closing-brace insert handler is shared by user defines and predefined variables.
        val insertClosingBrace = { ctx: com.intellij.codeInsight.completion.InsertionContext ->
            val tail = ctx.tailOffset
            val doc = ctx.document.charsSequence
            if (tail >= doc.length || doc[tail] != '}')
                ctx.document.insertString(tail, "}")
            ctx.editor.caretModel.moveToOffset(tail + 1)
        }

        file.definedConstants.forEach { (name, value) ->
            adjusted.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(name)
                        .withTypeText(value?.let { "= $it" } ?: "define")
                        .withIcon(IsIcons.Variable)
                        .withInsertHandler { ctx, _ -> insertClosingBrace(ctx) },
                    5.0
                )
            )
        }

        // Value-bearing ISPP predefined variables ({#__LINE__}, {#SourcePath}, …); the valueless
        // `void` symbols are excluded — they are only defined for #ifdef and cannot be emitted via {#…}.
        service<IsPreprocessorService>().emittableVariables.forEach { v ->
            adjusted.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(v.name)
                        .withTypeText("${v.type.typeName} · ISPP")
                        .withIcon(IsIcons.Variable)
                        .withInsertHandler { ctx, _ -> insertClosingBrace(ctx) },
                    3.0
                )
            )
        }
    }
}

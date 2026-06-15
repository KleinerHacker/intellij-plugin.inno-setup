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
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.services.IsConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.types.appliesTo

/**
 * Provides context-aware completion variants for Inno Setup PSI elements.
 */
object IsSectionConstantCompletionProvider : CompletionProvider<CompletionParameters>() {

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
        if (offset == 0 || chars[offset - 1] != '{') return

        val file = parameters.originalFile as? IsScriptFile ?: return
        val target = file.specTarget
        val builtins = service<IsConstantService>().spec.constants
        val userDefs = file.definedConstants

        builtins.forEach { const ->
            // Deprecation is shown via strikethrough (withStrikeoutness), not as tail text.
            val tail = if (const.parameterized) " (${const.syntax ?: "parameterized"})" else ""
            val element = LookupElementBuilder
                .create(if (const.parameterized && const.syntax?.startsWith("${const.name}:") == true) "${const.name}:" else const.name)
                .withTypeText(const.type.name.lowercase().replace('_', ' '))
                .withTailText(tail, true)
                .withStrikeoutness(const.deprecated.appliesTo(target))
                .withIcon(IsIcons.Constant)
                .withInsertHandler { ctx, _ ->
                    if (!const.parameterized)
                        ctx.document.insertString(ctx.tailOffset, "}")
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                }
            result.addElement(PrioritizedLookupElement.withPriority(element, 10.0))
        }

        userDefs.forEach { (name, value) ->
            val element = LookupElementBuilder
                .create("#$name")
                .withTypeText("define")
                .withTailText(value?.let { " = $it" } ?: "", true)
                .withIcon(IsIcons.Variable)
                .withInsertHandler { ctx, _ ->
                    ctx.document.insertString(ctx.tailOffset, "}")
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                }
            result.addElement(PrioritizedLookupElement.withPriority(element, 5.0))
        }

        service<IsPreprocessorService>().emittableVariables.forEach { v ->
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create("#${v.name}")
                        .withTypeText("${v.type.typeName} · ISPP")
                        .withIcon(IsIcons.Variable)
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, "}")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                        },
                    3.0
                )
            )
        }
    }
}

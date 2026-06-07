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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.services.IssConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IssPpService

object IsiConstantCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val offset = parameters.offset
        val chars = parameters.editor.document.charsSequence
        if (offset == 0 || chars[offset - 1] != '{') return

        val file = parameters.originalFile as? IssFile ?: return
        val builtins = service<IssConstantService>().spec.constants
        val userDefs = file.definedConstants()

        builtins.forEach { const ->
            val tail = if (const.parameterized) " (${const.syntax ?: "parameterized"})" else ""
            val element = LookupElementBuilder
                .create(if (const.parameterized) "${const.name}:" else const.name)
                .withTypeText(const.category.name.lowercase().replace('_', ' '))
                .withTailText(tail, true)
                .withIcon(IssIcons.Constant)
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
                .withIcon(IssIcons.Variable)
                .withInsertHandler { ctx, _ ->
                    ctx.document.insertString(ctx.tailOffset, "}")
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                }
            result.addElement(PrioritizedLookupElement.withPriority(element, 5.0))
        }

        service<IssPpService>().spec.predefinedVariables.forEach { v ->
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create("#${v.name}")
                        .withTypeText("${v.type} · ISPP")
                        .withIcon(IssIcons.Variable)
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

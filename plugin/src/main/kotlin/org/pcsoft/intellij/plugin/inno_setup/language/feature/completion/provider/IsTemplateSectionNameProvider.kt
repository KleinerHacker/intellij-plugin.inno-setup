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
import com.intellij.openapi.components.service
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.template.IsTemplateFile
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsSpecService

/**
 * Section-name completion after `[` in free-text `.ist` template files. Unlike the ISS/ISL provider
 * there is no language-file or version filtering — a template is free text, so all known section
 * names are offered.
 */
object IsTemplateSectionNameProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        parameters.originalFile as? IsTemplateFile ?: return

        service<IsSpecService>().spec.sections.forEach { specSection ->
            val element = LookupElementBuilder
                .create(specSection.name)
                .withTypeText(specSection.type.typeName)
                .withInsertHandler { ctx, _ ->
                    val tail = ctx.tailOffset
                    val chars = ctx.document.charsSequence
                    // If ] is already there (e.g. auto-paired by the IDE), skip past it.
                    if (tail < chars.length && chars[tail] == ']') {
                        ctx.editor.caretModel.moveToOffset(tail + 1)
                    } else {
                        ctx.document.insertString(tail, "]")
                        ctx.editor.caretModel.moveToOffset(tail + 1)
                    }
                }
            result.addElement(element)
        }
    }
}

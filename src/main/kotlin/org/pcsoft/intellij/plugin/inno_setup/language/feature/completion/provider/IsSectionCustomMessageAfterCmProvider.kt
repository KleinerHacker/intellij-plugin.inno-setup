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
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSections

/**
 * Completion of declared custom-message names inside the {cm:…} constant. Looks back from the
 * caret for the nearest `{cm:` and offers the message names declared in the file's
 * \[CustomMessages] section(s).
 */
object IsSectionCustomMessageAfterCmProvider : CompletionProvider<CompletionParameters>() {
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
        if (!afterBrace.regionMatches(0, "cm:", 0, 3, ignoreCase = true)) return
        val afterColon = afterBrace.substring(3)
        // Past the name (into the printf-style arguments) — nothing to complete.
        if (afterColon.contains(',')) return

        val file = parameters.originalFile as? IsScriptFile ?: return
        val adjusted = result.withPrefixMatcher(afterColon)

        customMessageNames(file).forEach { name ->
            adjusted.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(name)
                        .withTypeText("custom message")
                        .withIcon(IsIcons.Constant)
                        .withInsertHandler { ctx, _ ->
                            val tail = ctx.tailOffset
                            val doc = ctx.document.charsSequence
                            if (tail >= doc.length || doc[tail] != '}')
                                ctx.document.insertString(tail, "}")
                            ctx.editor.caretModel.moveToOffset(tail + 1)
                        },
                    10.0
                )
            )
        }
    }

    /** Distinct custom-message names declared in the file's \[CustomMessages] section(s), with any
     *  `lang.` prefix stripped. */
    private fun customMessageNames(file: IsScriptFile): List<String> =
        file.findSections("CustomMessages")
            .flatMap { it.directiveEntryList }
            .map { it.keyText().substringAfterLast('.') }
            .filter { it.isNotEmpty() }
            .distinct()
}

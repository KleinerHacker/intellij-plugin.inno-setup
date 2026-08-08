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
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorPragmaArgument
import org.pcsoft.intellij.plugin.inno_setup.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.lang.IsLanguageFile

/**
 * Completion inside a `#pragma` directive:
 *
 * - Directly after `#pragma ` the sub-commands (`option`, `message`, `verboselevel`, …) are suggested.
 * - After `#pragma option ` / `#pragma parseroption ` the accepted option flags (`-c+`, `-v-`, …) are
 *   suggested, with their meaning as tail text.
 *
 * Expression arguments (`#pragma message …`) are handled by the shared expression providers
 * ([IsPreprocessorDefineExpressionProvider] / [IsPreprocessorBuiltinFunctionProvider]) via
 * [IsPreprocessorExpressionContext].
 */
object IsPreprocessorPragmaProvider : CompletionProvider<CompletionParameters>() {

    // After the #pragma keyword, while typing the sub-command (no further token yet).
    private val SUB_COMMAND_PREFIX = Regex("^#\\s*pragma\\s+([A-Za-z]*)$", RegexOption.IGNORE_CASE)

    // In the flag area of an option / parseroption pragma.
    private val FLAG_PREFIX = Regex("^#\\s*pragma\\s+(option|parseroption)\\s+.*$", RegexOption.IGNORE_CASE)

    // The flag token currently being typed (e.g. `-v`).
    private val FLAG_TAIL = Regex("[-A-Za-z+]*$")
    private val WORD_TAIL = Regex("[A-Za-z]*$")

    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val hostFile = InjectedLanguageManager.getInstance(params.position.project)
            .getTopLevelFile(params.originalFile)
        if (hostFile is IsLanguageFile) return

        val offset = params.offset
        val doc = params.editor.document
        val lineStart = doc.getLineStartOffset(doc.getLineNumber(offset))
        val linePrefix = doc.charsSequence.subSequence(lineStart, offset).toString()

        val spec = service<IsPreprocessorService>().spec.pragmaSubCommands

        SUB_COMMAND_PREFIX.find(linePrefix)?.let {
            val typed = WORD_TAIL.find(linePrefix)?.value ?: ""
            val adjusted = result.withPrefixMatcher(typed)
            spec.forEach { sub ->
                adjusted.addElement(
                    LookupElementBuilder.create(sub.name)
                        .withTypeText("ISPP")
                        .withTailText("  ${sub.syntax}", true)
                        .withIcon(IsIcons.Constant)
                        .withInsertHandler { ctx, _ ->
                            if (sub.argument != IsPreprocessorPragmaArgument.NONE) {
                                ctx.document.insertString(ctx.tailOffset, " ")
                                ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                            }
                        }
                )
            }
            return
        }

        FLAG_PREFIX.find(linePrefix)?.let { match ->
            val subName = match.groupValues[1]
            val sub = spec.firstOrNull { it.name.equals(subName, ignoreCase = true) } ?: return
            val typed = FLAG_TAIL.find(linePrefix)?.value ?: ""
            val adjusted = result.withPrefixMatcher(typed)
            sub.flagLetters.forEach { flag ->
                listOf("+", "-").forEach { sign ->
                    adjusted.addElement(
                        LookupElementBuilder.create("-${flag.letter}$sign")
                            .withTypeText(if (flag.default) "default on" else "default off")
                            .withTailText("  ${flag.description}", true)
                            .withIcon(IsIcons.Constant)
                    )
                }
            }
        }
    }
}

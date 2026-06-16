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
import com.intellij.openapi.components.service
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService

/**
 * Inside a `#define` expression, suggest the built-in ISPP functions (e.g. `Len`, `Copy`, `FileExists`) from
 * the bundled spec, with their parameter list as tail text and their return type as type text. Shares the
 * expression-context gating with [IsPreprocessorDefineExpressionProvider] via [IsPreprocessorExpressionContext].
 */
object IsPreprocessorBuiltinFunctionProvider : CompletionProvider<CompletionParameters>() {

    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val adjusted = IsPreprocessorExpressionContext.adjustedResult(params, result) ?: return

        service<IsPreprocessorService>().spec.builtinFunctions.forEach { function ->
            adjusted.addElement(
                LookupElementBuilder.create(function.name)
                    .withTailText(functionParams(function.signature), true)
                    .withTypeText(function.returnType.typeName)
                    .withIcon(IsIcons.Function)
            )
        }
    }

    /** The parameter list of a function signature (e.g. `(S: str, Index: int)`), or `()` when absent. */
    private fun functionParams(signature: String): String {
        val open = signature.indexOf('(')
        val close = signature.lastIndexOf(')')
        return if (open in 0 until close) signature.substring(open, close + 1) else "()"
    }
}

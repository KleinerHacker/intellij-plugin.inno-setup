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
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.appliesTo
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.*
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.script.types.IsSectionFlagTypeSpec

/**
 * Offers the available flags of a `kind: flags` attribute as completion suggestions.
 *
 * The popup appears right after the `:` (or `=`) and after any already typed flag — leading and
 * trailing whitespace between flags is irrelevant because the suggestions are bound to the enclosing
 * [IsSectionParamValue], not to a particular caret offset. Flags already present in the same value are
 * filtered out so only the still-available ones are offered.
 */
object IsSectionFlagValueProvider : CompletionProvider<CompletionParameters>() {
    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection) return
        val paramValue = PsiTreeUtil.getParentOfType(position, IsSectionParamValue::class.java) ?: return

        val spec = service<IsSpecService>().spec
        val attr = run {
            val pair = paramValue.containingParamPair
            if (pair != null) {
                val ss = pair.containingSection?.specSection(spec) ?: return
                ss.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
            } else {
                val dir = paramValue.containingDirectiveEntry ?: return
                val ss = dir.containingSection?.specSection(spec) ?: return
                ss.attributes.firstOrNull { it.name.equals(dir.keyText(), ignoreCase = true) }
            }
        } ?: return

        val flagType = attr.type as? IsSectionFlagTypeSpec ?: return
        val target = paramValue.specTarget

        // Flags already present in the value (excluding the token currently being typed at the caret).
        val present = paramValue.node
            .getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
            .filter { it.psi != position }
            .map { it.text.lowercase() }
            .toSet()

        flagType.flags
            .filter { it.name.lowercase() !in present }
            .forEach { flag ->
                // Deprecation is shown via strikethrough (withStrikeoutness), not as tail text.
                val element = LookupElementBuilder.create(flag.name)
                    .withStrikeoutness(flag.deprecated.appliesTo(target))
                result.addElement(PrioritizedLookupElement.withPriority(element, 20.0))
            }
    }
}

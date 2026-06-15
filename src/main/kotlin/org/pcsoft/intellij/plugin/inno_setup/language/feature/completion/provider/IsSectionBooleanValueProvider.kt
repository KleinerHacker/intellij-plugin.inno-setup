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
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.*
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.IsSectionSyntaxHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionNativeDataType
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionNativeTypeSpec

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
object IsSectionBooleanValueProvider : CompletionProvider<CompletionParameters>() {
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

        val type = attr.type as? IsSectionNativeTypeSpec ?: return
        if (type.dataType != IsSectionNativeDataType.BOOLEAN) return

        listOf("yes", "no").forEach { value ->
            result.addElement(
                PrioritizedLookupElement.withPriority(keywordLookupElement(value), 20.0)
            )
        }
    }

    private fun keywordLookupElement(value: String): LookupElement = object : LookupElement() {
        override fun getLookupString() = value
        override fun renderElement(presentation: LookupElementPresentation) {
            presentation.itemText = value
            presentation.isItemTextBold = true
            presentation.typeText = "boolean"
            val attrs = EditorColorsManager.getInstance().globalScheme
                .getAttributes(IsSectionSyntaxHighlighting.KEYWORD)
            presentation.itemTextForeground = attrs?.foregroundColor ?: JBColor.foreground()
        }
    }
}

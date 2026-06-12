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
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.issFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.*
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamValue

object IsSectionReferenceValueProvider : CompletionProvider<CompletionParameters>() {
    private val KEY_TO_SECTION = mapOf(
        "tasks" to "Tasks",
        "components" to "Components",
        "types" to "Types",
        "languages" to "Languages",
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection) return
        val paramValue = PsiTreeUtil.getParentOfType(position, IsSectionParamValue::class.java) ?: return
        val pair = paramValue.containingParamPair as? IsSectionParamPairEx ?: return
        val targetSection = KEY_TO_SECTION[pair.keyText().lowercase()] ?: return
        val file = paramValue.issFile ?: return
        file.findSections(targetSection)
            .flatMap { it.nameDeclarations }
            .mapNotNull { it.valueUnquoted.ifEmpty { null } }
            .forEach { name -> result.addElement(LookupElementBuilder.create(name)) }
    }
}
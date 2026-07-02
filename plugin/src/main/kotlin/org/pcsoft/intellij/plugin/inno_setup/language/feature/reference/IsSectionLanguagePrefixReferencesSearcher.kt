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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.containingSection
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamPairEx

/**
 * Finds `lang.` key-prefix usages (in \[Messages]/\[CustomMessages]) of a `\[Languages] Name`
 * declaration so Find Usages and Rename work. Like [IsSectionReferencesSearcher], the
 * word-occurrence search calls getReferences() on the leaf IDENTIFIER (which returns nothing) — the
 * reference lives on the parent [IsSectionDirectiveEntry] — so this executor scans those entries directly.
 * `Languages:` parameter usages remain covered by [IsSectionReferencesSearcher].
 */
class IsSectionLanguagePrefixReferencesSearcher : QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {
    /**
     * Processes the search request and reports matching references to the consumer.
     */
    override fun execute(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>,
    ): Boolean {
        val pair = queryParameters.elementToSearch as? IsSectionParamPairEx ?: return true
        if (!pair.isNameDeclaration()) return true

        val element = pair as? PsiElement ?: return true
        if (element.containingSection?.nameText?.equals("Languages", ignoreCase = true) != true) return true

        val file = element.containingFile as? IsScriptFile ?: return true

        for (entry in PsiTreeUtil.findChildrenOfType(file, IsSectionDirectiveEntry::class.java)) {
            for (ref in entry.references) {
                if (ref.isReferenceTo(element) && !consumer.process(ref)) {
                    return false
                }
            }
        }

        return true
    }
}

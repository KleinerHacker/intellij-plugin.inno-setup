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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPairEx

/**
 * Finds `lang.` key-prefix usages (in [Messages]/[CustomMessages]) of a `[Languages] Name`
 * declaration so Find Usages and Rename work. Like [IsiSectionReferencesSearcher], the
 * word-occurrence search calls getReferences() on the leaf IDENTIFIER (which returns nothing) — the
 * reference lives on the parent [IsiDirectiveEntry] — so this executor scans those entries directly.
 * `Languages:` parameter usages remain covered by [IsiSectionReferencesSearcher].
 */
class IsiLanguagePrefixReferencesSearcher : QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {
    override fun execute(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>,
    ): Boolean {
        val pair = queryParameters.elementToSearch as? IsiParamPairEx ?: return true
        if (!pair.isNameDeclaration()) return true
        val element = pair as? PsiElement ?: return true
        if (element.containingSection()?.nameText()?.equals("Languages", ignoreCase = true) != true) return true
        val file = element.containingFile as? IssFile ?: return true

        for (entry in PsiTreeUtil.findChildrenOfType(file, IsiDirectiveEntry::class.java)) {
            for (ref in entry.references) {
                if (ref.isReferenceTo(element) && !consumer.process(ref)) {
                    return false
                }
            }
        }
        return true
    }
}

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

import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamValue

// Mirrors IssIsppReferencesSearcher for section cross-references.
// IntelliJ's word-occurrence search calls getReferences() on the leaf IDENTIFIER, which
// returns nothing — the reference lives on the parent IsiParamValue.  This executor
// directly scans all IsiParamValue elements in the file so rename finds and updates
// Tasks:/Components:/Types:/Languages: references when a Name: value is renamed.
class IsiSectionReferencesSearcher : QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {
    override fun execute(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>,
    ): Boolean {
        val pair = queryParameters.elementToSearch as? IsiParamPairEx ?: return true
        if (!pair.isNameDeclaration()) return true
        val file = (pair as? com.intellij.psi.PsiElement)?.containingFile as? IssFile ?: return true

        for (pv in PsiTreeUtil.findChildrenOfType(file, IsiParamValue::class.java)) {
            for (ref in pv.references) {
                if (ref.isReferenceTo(pair as com.intellij.psi.PsiElement) && !consumer.process(ref)) {
                    return false
                }
            }
        }
        return true
    }
}

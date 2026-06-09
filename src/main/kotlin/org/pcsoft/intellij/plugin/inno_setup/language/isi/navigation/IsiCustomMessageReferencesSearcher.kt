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
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveEntryEx

/**
 * Finds `{cm:MessageName}` usages of a [CustomMessages] declaration so Find Usages and Rename
 * work. Like [IsiSectionReferencesSearcher], the word-occurrence search calls getReferences() on
 * the leaf IDENTIFIER (which returns nothing) — the reference lives on the parent
 * [IsiConstantBody] — so this executor scans those bodies directly.
 *
 * Other-language declarations of the same message are kept in sync by the declaration's own
 * setName (see IsiDirectiveEntryMixinImpl), not reported here.
 */
class IsiCustomMessageReferencesSearcher : QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {
    override fun execute(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>,
    ): Boolean {
        val entry = queryParameters.elementToSearch as? IsiDirectiveEntryEx ?: return true
        if (!entry.isCustomMessageDeclaration()) return true

        val file = (entry as? PsiElement)?.containingFile as? IssFile ?: return true

        for (body in PsiTreeUtil.findChildrenOfType(file, IsiConstantBody::class.java)) {
            for (ref in body.references) {
                if (ref.isReferenceTo(entry as PsiElement) && !consumer.process(ref)) {
                    return false
                }
            }
        }

        return true
    }
}

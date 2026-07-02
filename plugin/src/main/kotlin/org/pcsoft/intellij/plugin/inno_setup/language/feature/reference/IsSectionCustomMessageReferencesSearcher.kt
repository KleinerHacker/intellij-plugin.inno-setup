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
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionConstantBody
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionDirectiveEntryEx

/**
 * Finds `{cm:MessageName}` usages of a \[CustomMessages] declaration so Find Usages and Rename
 * work. Like [IsSectionReferencesSearcher], the word-occurrence search calls getReferences() on
 * the leaf IDENTIFIER (which returns nothing) — the reference lives on the parent
 * [IsSectionConstantBody] — so this executor scans those bodies directly.
 *
 * Other-language declarations of the same message are kept in sync by the declaration's own
 * setName (see IsSectionDirectiveEntryMixinImpl), not reported here.
 */
class IsSectionCustomMessageReferencesSearcher : QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {
    /**
     * Processes the search request and reports matching references to the consumer.
     */
    override fun execute(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>,
    ): Boolean {
        val entry = queryParameters.elementToSearch as? IsSectionDirectiveEntryEx ?: return true
        if (!entry.isCustomMessageDeclaration()) return true

        val file = (entry as? PsiElement)?.containingFile as? IsScriptFile ?: return true

        for (body in PsiTreeUtil.findChildrenOfType(file, IsSectionConstantBody::class.java)) {
            for (ref in body.references) {
                if (ref.isReferenceTo(entry as PsiElement) && !consumer.process(ref)) {
                    return false
                }
            }
        }

        return true
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.language.ispp.navigation

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.isppDirectives
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx

// When an IsppDirective (#define) is renamed, this searcher finds all references so they are
// renamed too: both {#Name} references in the host ISS file and free-text references inside the
// expressions of other #define directives.
class IsppReferencesSearcher : QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {

    override fun execute(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>,
    ): Boolean {
        val element = queryParameters.elementToSearch as? IsppDirectiveEx ?: return true
        if (!element.isDefine()) return true

        // The IsppDirective lives in an injected fragment; find the top-level ISS file.
        val hostFile = InjectedLanguageManager.getInstance(element.project)
            .getTopLevelFile(element.containingFile) as? IssFile ?: return true

        // {#Name} references in the host ISS file.
        for (body in PsiTreeUtil.findChildrenOfType(hostFile, IsiConstantBody::class.java)) {
            for (ref in body.references) {
                if (ref.isReferenceTo(element) && !consumer.process(ref)) return false
            }
        }

        // Free-text references inside other #define expressions.
        for (directive in hostFile.isppDirectives()) {
            for (ref in directive.references) {
                if (ref.isReferenceTo(element) && !consumer.process(ref)) return false
            }
        }
        return true
    }
}

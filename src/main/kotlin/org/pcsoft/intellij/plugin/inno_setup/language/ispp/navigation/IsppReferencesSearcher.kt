package org.pcsoft.intellij.plugin.inno_setup.language.ispp.navigation

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx

// When an IsppDirective (#define) is renamed, this searcher finds all {#Name}
// references in the host ISS file so they are also renamed.
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

        for (body in PsiTreeUtil.findChildrenOfType(hostFile, IssConstantBody::class.java)) {
            for (ref in body.references) {
                if (ref.isReferenceTo(element) && !consumer.process(ref)) return false
            }
        }
        return true
    }
}

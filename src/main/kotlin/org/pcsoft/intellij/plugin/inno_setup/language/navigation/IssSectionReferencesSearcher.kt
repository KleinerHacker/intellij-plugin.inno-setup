package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamValue

// Mirrors IssIsppReferencesSearcher for section cross-references.
// IntelliJ's word-occurrence search calls getReferences() on the leaf IDENTIFIER, which
// returns nothing — the reference lives on the parent IssParamValue.  This executor
// directly scans all IssParamValue elements in the file so rename finds and updates
// Tasks:/Components:/Types:/Languages: references when a Name: value is renamed.
class IssSectionReferencesSearcher : QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {
    override fun execute(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>,
    ): Boolean {
        val pair = queryParameters.elementToSearch as? IssParamPairEx ?: return true
        if (!pair.isNameDeclaration()) return true
        val file = (pair as? com.intellij.psi.PsiElement)?.containingFile as? IssFile ?: return true

        for (pv in PsiTreeUtil.findChildrenOfType(file, IssParamValue::class.java)) {
            for (ref in pv.references) {
                if (ref.isReferenceTo(pair as com.intellij.psi.PsiElement) && !consumer.process(ref)) {
                    return false
                }
            }
        }
        return true
    }
}

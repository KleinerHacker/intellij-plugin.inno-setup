package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirectiveEx

// IntelliJ's default ReferencesSearch calls getReferences() on the leaf IDENTIFIER node,
// which returns nothing because the reference lives on the parent IssConstantBody.
// This executor bypasses word-occurrence search and directly scans the file's IssConstantBody
// elements so that rename finds and updates {#Name} references correctly.
class IssIsppReferencesSearcher : QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {
    override fun execute(
        queryParameters: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>,
    ): Boolean {
        val element = queryParameters.elementToSearch as? IssPreprocessorDirectiveEx ?: return true
        if (!element.isDefine()) return true
        val file = element.containingFile as? IssFile ?: return true

        for (body in PsiTreeUtil.findChildrenOfType(file, IssConstantBody::class.java)) {
            for (ref in body.references) {
                if (ref.isReferenceTo(element) && !consumer.process(ref)) return false
            }
        }
        return true
    }
}

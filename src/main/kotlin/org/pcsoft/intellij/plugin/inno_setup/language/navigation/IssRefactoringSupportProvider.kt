package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPairEx

class IssRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
        element is IssParamPairEx && element.isNameDeclaration()
}

package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPairEx

class IsiRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
        element is IsiParamPairEx && element.isNameDeclaration()
}

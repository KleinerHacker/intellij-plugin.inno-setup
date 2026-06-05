package org.pcsoft.intellij.plugin.inno_setup.language.ispp.navigation

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx

class IsppRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
        element is IsppDirectiveEx && element.isDefine()
}

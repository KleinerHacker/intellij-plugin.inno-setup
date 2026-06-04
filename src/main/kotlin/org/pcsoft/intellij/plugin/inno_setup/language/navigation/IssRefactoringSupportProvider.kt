package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirectiveEx

class IssRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
        (element is IssPreprocessorDirectiveEx && element.isDefine()) ||
        (element is IssParamPairEx && element.isNameDeclaration())
}

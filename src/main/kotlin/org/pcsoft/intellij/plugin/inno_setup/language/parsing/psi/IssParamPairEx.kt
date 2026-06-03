package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi

import com.intellij.psi.PsiElement

interface IssParamPairEx : PsiElement {
    fun keyText(): String
    fun isNameDeclaration(): Boolean
    fun isReferenceParam(): Boolean
}

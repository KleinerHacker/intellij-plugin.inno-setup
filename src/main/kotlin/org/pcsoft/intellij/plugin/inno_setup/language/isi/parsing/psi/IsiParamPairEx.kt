package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi

import com.intellij.psi.PsiElement

interface IsiParamPairEx : PsiElement {
    fun keyText(): String
    fun isNameDeclaration(): Boolean
    fun isReferenceParam(): Boolean
}

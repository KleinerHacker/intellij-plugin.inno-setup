package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

interface IsppDirectiveEx : PsiNameIdentifierOwner {
    fun isDefine(): Boolean
    fun getDefineName(): String?
    fun getDefineTypeName(): String?
    fun getDefineTypeIdentifier(): PsiElement?
    fun getDefineValue(): String?
}

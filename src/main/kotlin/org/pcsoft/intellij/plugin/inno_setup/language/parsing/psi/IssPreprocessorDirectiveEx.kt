package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi

import com.intellij.psi.PsiNameIdentifierOwner

interface IssPreprocessorDirectiveEx : PsiNameIdentifierOwner {
    fun isDefine(): Boolean
    fun getDefineName(): String?
}

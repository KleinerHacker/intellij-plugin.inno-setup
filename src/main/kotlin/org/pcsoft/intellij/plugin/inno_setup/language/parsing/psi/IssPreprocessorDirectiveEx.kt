package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi

import com.intellij.psi.PsiNameIdentifierOwner

interface IssPreprocessorDirectiveEx : PsiNameIdentifierOwner {
    fun isDefine(): Boolean
    fun getDefineName(): String?
    /** Returns the type keyword ("int", "str", "float") for typed defines, null otherwise. */
    fun getDefineTypeName(): String?
}

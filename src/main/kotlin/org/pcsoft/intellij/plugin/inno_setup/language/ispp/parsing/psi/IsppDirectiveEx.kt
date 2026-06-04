package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi

import com.intellij.psi.PsiNameIdentifierOwner

interface IsppDirectiveEx : PsiNameIdentifierOwner {
    fun isDefine(): Boolean
    fun getDefineName(): String?
    fun getDefineValue(): String?

    /** A function-like macro: the name is immediately followed by `(` (no whitespace). */
    fun isFunctionMacro(): Boolean

    /** For function-like macros: the expression after the `(…)` parameter list, or `null` if absent. */
    fun getMacroBody(): String?
}

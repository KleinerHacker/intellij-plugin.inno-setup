package org.pcsoft.intellij.plugin.inno_setup.language.ispp

import com.intellij.lang.Language

object IsppLanguage : Language("ISPP") {
    private fun readResolve(): Any = IsppLanguage
}

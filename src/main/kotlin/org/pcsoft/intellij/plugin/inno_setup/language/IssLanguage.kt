package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.lang.Language

object IssLanguage : Language("ISS") {
    private fun readResolve(): Any = IssLanguage
}

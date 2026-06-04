package org.pcsoft.intellij.plugin.inno_setup.language.ispp

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class IsppFileType private constructor() : LanguageFileType(IsppLanguage) {
    companion object {
        @JvmField
        val INSTANCE = IsppFileType()
    }

    override fun getName(): String = "Inno Setup Preprocessor"
    override fun getDescription(): String = "Inno Setup Preprocessor fragment"
    override fun getDefaultExtension(): String = "ispp"
    override fun getIcon(): Icon? = null
}

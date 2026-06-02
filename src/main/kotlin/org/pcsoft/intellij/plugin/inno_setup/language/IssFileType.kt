package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.openapi.fileTypes.LanguageFileType
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import javax.swing.Icon

class IssFileType : LanguageFileType(IssLanguage) {
    companion object {
        @JvmField
        val INSTANCE = IssFileType()
    }

    override fun getName() = "Inno Setup Script"
    override fun getDescription() = "Inno Setup script file"
    override fun getDefaultExtension() = "iss"
    override fun getIcon(): Icon = IssIcons.ScriptFile
}

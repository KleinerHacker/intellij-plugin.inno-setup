package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object IssFileType : LanguageFileType(IssLanguage) {
    private fun readResolve(): Any = IssFileType
    override fun getName() = "Inno Setup Script"
    override fun getDescription() = "Inno Setup script file"
    override fun getDefaultExtension() = "iss"
    override fun getIcon(): Icon = IconLoader.getIcon("/icons/inno-setup-script-icon.svg", IssFileType::class.java)
}

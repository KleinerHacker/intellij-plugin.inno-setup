package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class IssFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, IssLanguage) {
    override fun getFileType() = IssFileType.INSTANCE
}

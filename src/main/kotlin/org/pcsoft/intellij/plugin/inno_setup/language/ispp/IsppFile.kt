package org.pcsoft.intellij.plugin.inno_setup.language.ispp

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class IsppFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, IsppLanguage) {
    override fun getFileType() = IsppFileType.INSTANCE
}

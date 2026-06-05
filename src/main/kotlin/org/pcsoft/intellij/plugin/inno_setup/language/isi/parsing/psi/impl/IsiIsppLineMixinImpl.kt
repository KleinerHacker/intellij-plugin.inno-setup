package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiIsppLine

abstract class IsiIsppLineMixinImpl(node: ASTNode)
    : ASTWrapperPsiElement(node), PsiLanguageInjectionHost {

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val factory = PsiFileFactory.getInstance(project)
        val newFile = factory.createFileFromText("d.iss", IssFileType.INSTANCE, "$text\n")
        val newLine = PsiTreeUtil.findChildOfType(newFile, IsiIsppLine::class.java) ?: return this
        return replace(newLine) as? PsiLanguageInjectionHost ?: this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> =
        LiteralTextEscaper.createSimple(this as IsiIsppLine)
}

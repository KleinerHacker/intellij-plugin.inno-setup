package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssIsppLine

abstract class IssIsppLineMixinImpl(node: ASTNode)
    : ASTWrapperPsiElement(node), PsiLanguageInjectionHost {

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val factory = PsiFileFactory.getInstance(project)
        val newFile = factory.createFileFromText("d.iss", IssFileType.INSTANCE, "$text\n")
        val newLine = PsiTreeUtil.findChildOfType(newFile, IssIsppLine::class.java) ?: return this
        return replace(newLine) as? PsiLanguageInjectionHost ?: this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> =
        LiteralTextEscaper.createSimple(this as IssIsppLine)
}

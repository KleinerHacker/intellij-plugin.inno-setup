package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

abstract class IssPreprocessorDirectiveMixinImpl(node: ASTNode)
    : ASTWrapperPsiElement(node), IssPreprocessorDirectiveEx {

    override fun isDefine() =
        (this as IssPreprocessorDirective).identifier?.text
            ?.equals("define", ignoreCase = true) == true

    override fun getDefineName(): String? {
        if (!isDefine()) return null
        return (this as IssPreprocessorDirective).paramValue
            ?.node?.findChildByType(IssTypes.IDENTIFIER)?.text
    }

    override fun getName() = getDefineName()

    override fun setName(name: String): PsiElement {
        val oldId = (this as IssPreprocessorDirective).paramValue
            ?.node?.findChildByType(IssTypes.IDENTIFIER)?.psi ?: return this
        val dummy = PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "#define $name\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IssPreprocessorDirective::class.java)
            ?.paramValue?.node?.findChildByType(IssTypes.IDENTIFIER)?.psi ?: return this
        oldId.replace(newId)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        if (!isDefine()) return null
        return (this as IssPreprocessorDirective).paramValue
            ?.node?.findChildByType(IssTypes.IDENTIFIER)?.psi
    }

    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()
}

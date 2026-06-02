package org.pcsoft.intellij.plugin.inno_setup.language.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssNamedElement
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssTypes

abstract class IssNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), IssNamedElement {

    override fun getName(): String? =
        node.findChildByType(IssTypes.IDENTIFIER)?.text
            ?: node.findChildByType(IssTypes.STRING)?.text?.trim('"')

    override fun setName(name: String): PsiElement {
        val id = node.findChildByType(IssTypes.IDENTIFIER)
            ?: node.findChildByType(IssTypes.STRING)
            ?: return this
        val newEl = com.intellij.psi.PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.iss", IssFileType, "[Tasks]\nName: $name\n") as IssFile
        val newId = newEl.node.findChildByType(IssTypes.IDENTIFIER) ?: return this
        node.replaceChild(id, newId)
        return this
    }

    override fun getNameIdentifier(): PsiElement? =
        node.findChildByType(IssTypes.IDENTIFIER)?.psi
            ?: node.findChildByType(IssTypes.STRING)?.psi
}

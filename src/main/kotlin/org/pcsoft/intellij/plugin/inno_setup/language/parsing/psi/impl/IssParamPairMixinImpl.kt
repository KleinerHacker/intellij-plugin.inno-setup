package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

abstract class IssParamPairMixinImpl(node: ASTNode)
    : ASTWrapperPsiElement(node), IssParamPairEx, PsiNameIdentifierOwner {

    private val REFERENCE_KEYS = setOf("tasks", "components", "types", "languages")

    override fun keyText(): String =
        node.findChildByType(IssTypes.PARAM_KEY)?.psi?.text.orEmpty()

    override fun isNameDeclaration(): Boolean =
        keyText().equals("Name", ignoreCase = true)

    override fun isReferenceParam(): Boolean =
        keyText().lowercase() in REFERENCE_KEYS

    // PsiNameIdentifierOwner — only meaningful for Name: <value> pairs
    override fun getName(): String? {
        if (!isNameDeclaration()) return null
        return (this as IssParamPair).paramValue?.text?.trim()?.removeSurrounding("\"")
    }

    override fun setName(name: String): PsiElement {
        if (!isNameDeclaration()) return this
        val paramValue = (this as IssParamPair).paramValue ?: return this
        val oldId = paramValue.node.findChildByType(IssTypes.IDENTIFIER)?.psi ?: return this
        // Use Tasks dummy in a known valid context to produce the target IDENTIFIER node.
        val dummy = PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "[Tasks]\nName: $name\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IssParamValue::class.java)
            ?.node?.findChildByType(IssTypes.IDENTIFIER)?.psi ?: return this
        oldId.replace(newId)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        if (!isNameDeclaration()) return null
        return (this as IssParamPair).paramValue?.node?.findChildByType(IssTypes.IDENTIFIER)?.psi
    }

    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()
}

package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import org.pcsoft.intellij.plugin.inno_setup.language.navigation.IssIsppConstantReference
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

abstract class IssConstantBodyMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReferences(): Array<PsiReference> {
        val bodyText = text ?: return PsiReference.EMPTY_ARRAY
        if (!bodyText.startsWith("#")) return PsiReference.EMPTY_ARRAY
        val nameNode = node.findChildByType(IssTypes.IDENTIFIER) ?: return PsiReference.EMPTY_ARRAY
        val name = nameNode.text
        if (name.isEmpty()) return PsiReference.EMPTY_ARRAY
        return arrayOf(IssIsppConstantReference(this as IssConstantBody, name))
    }
}

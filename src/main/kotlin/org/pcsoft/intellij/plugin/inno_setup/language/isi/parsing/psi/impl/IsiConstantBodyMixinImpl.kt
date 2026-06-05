package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation.IsiIsppConstantReference
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

abstract class IsiConstantBodyMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReferences(): Array<PsiReference> {
        val bodyText = text ?: return PsiReference.EMPTY_ARRAY
        if (!bodyText.startsWith("#")) return PsiReference.EMPTY_ARRAY
        val nameNode = node.findChildByType(IsiTypes.IDENTIFIER) ?: return PsiReference.EMPTY_ARRAY
        val name = nameNode.text
        if (name.isEmpty()) return PsiReference.EMPTY_ARRAY
        return arrayOf(IsiIsppConstantReference(this as IsiConstantBody, name))
    }
}

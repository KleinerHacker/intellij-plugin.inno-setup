package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.navigation.IssReference
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

abstract class IssParamValueMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

    companion object {
        private val REF_KEY_TO_SECTION = mapOf(
            "tasks" to "Tasks",
            "components" to "Components",
            "types" to "Types",
            "languages" to "Languages",
        )
    }

    override fun getReferences(): Array<PsiReference> {
        val pair = containingParamPair() ?: return PsiReference.EMPTY_ARRAY
        val targetSection = REF_KEY_TO_SECTION[pair.keyText().lowercase()] ?: return PsiReference.EMPTY_ARRAY
        val paramValue = this as IssParamValue
        return node.getChildren(TokenSet.create(IssTypes.IDENTIFIER))
            .map { idNode ->
                val start = idNode.startOffset - textOffset
                IssReference(paramValue, TextRange(start, start + idNode.textLength), targetSection)
            }
            .toTypedArray()
    }
}

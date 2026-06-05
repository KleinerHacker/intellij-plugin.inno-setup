package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation.IsiReference
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

abstract class IsiParamValueMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node) {

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
        val paramValue = this as IsiParamValue
        return node.getChildren(TokenSet.create(IsiTypes.IDENTIFIER))
            .map { idNode ->
                val start = idNode.startOffset - textOffset
                IsiReference(paramValue, TextRange(start, start + idNode.textLength), targetSection)
            }
            .toTypedArray()
    }
}

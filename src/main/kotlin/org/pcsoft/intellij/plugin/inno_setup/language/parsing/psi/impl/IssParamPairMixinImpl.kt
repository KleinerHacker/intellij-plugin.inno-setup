package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

abstract class IssParamPairMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node), IssParamPairEx {

    private val REFERENCE_KEYS = setOf("tasks", "components", "types", "languages")

    override fun keyText(): String =
        node.findChildByType(IssTypes.PARAM_KEY)?.psi?.text.orEmpty()

    override fun isNameDeclaration(): Boolean =
        keyText().equals("Name", ignoreCase = true)

    override fun isReferenceParam(): Boolean =
        keyText().lowercase() in REFERENCE_KEYS
}

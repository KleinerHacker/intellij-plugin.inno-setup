package org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

abstract class IssPreprocessorDirectiveMixinImpl(node: ASTNode)
    : ASTWrapperPsiElement(node), IssPreprocessorDirectiveEx {

    companion object {
        // ISPP optional type qualifier that may precede the constant name:
        //   #define int  Name 42
        //   #define str  Name "hello"
        //   #define float Name 3.14
        val TYPE_KEYWORDS = setOf("int", "integer", "str", "string", "float", "double", "any")
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun paramValueIdentifiers(): Array<ASTNode> =
        (this as IssPreprocessorDirective).paramValue
            ?.node?.getChildren(TokenSet.create(IssTypes.IDENTIFIER))
            ?: emptyArray()

    /** True when the first IDENTIFIER in paramValue is a type qualifier. */
    private fun hasTypePrefix(): Boolean {
        val ids = paramValueIdentifiers()
        return ids.size >= 2 && ids[0].text.lowercase() in TYPE_KEYWORDS
    }

    // ── IssPreprocessorDirectiveEx ────────────────────────────────────────────

    override fun isDefine() =
        (this as IssPreprocessorDirective).identifier?.text
            ?.equals("define", ignoreCase = true) == true

    override fun getDefineTypeName(): String? {
        if (!isDefine()) return null
        val ids = paramValueIdentifiers()
        if (ids.size < 2) return null
        val first = ids[0].text
        return if (first.lowercase() in TYPE_KEYWORDS) first else null
    }

    override fun getDefineName(): String? {
        if (!isDefine()) return null
        val ids = paramValueIdentifiers()
        if (ids.isEmpty()) return null
        // Skip the type qualifier when present
        return if (hasTypePrefix()) ids[1].text else ids[0].text
    }

    // ── PsiNameIdentifierOwner ────────────────────────────────────────────────

    override fun getName() = getDefineName()

    override fun setName(name: String): PsiElement {
        val oldId = getNameIdentifier() ?: return this
        val dummy = PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "#define $name\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IssPreprocessorDirective::class.java)
            ?.paramValue?.node?.findChildByType(IssTypes.IDENTIFIER)?.psi ?: return this
        oldId.replace(newId)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        if (!isDefine()) return null
        val ids = paramValueIdentifiers()
        if (ids.isEmpty()) return null
        return if (hasTypePrefix()) ids[1].psi else ids[0].psi
    }

    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()
}

package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes

abstract class IsppDirectiveMixinImpl(node: ASTNode)
    : ASTWrapperPsiElement(node), IsppDirectiveEx {

    companion object {
        val TYPE_KEYWORDS = setOf("int", "integer", "str", "string", "float", "double", "any")
    }

    private fun valueIdentifiers(): Array<ASTNode> =
        (this as IsppDirective).value
            ?.node?.getChildren(TokenSet.create(IsppTypes.IDENTIFIER))
            ?: emptyArray()

    private fun hasTypePrefix(): Boolean {
        val ids = valueIdentifiers()
        return ids.size >= 2 && ids[0].text.lowercase() in TYPE_KEYWORDS
    }

    override fun isDefine(): Boolean =
        (this as IsppDirective).identifier?.text?.equals("define", ignoreCase = true) == true

    override fun getDefineTypeName(): String? {
        if (!isDefine()) return null
        val ids = valueIdentifiers()
        if (ids.size < 2) return null
        val first = ids[0].text
        return if (first.lowercase() in TYPE_KEYWORDS) first else null
    }

    override fun getDefineName(): String? {
        if (!isDefine()) return null
        val ids = valueIdentifiers()
        if (ids.isEmpty()) return null
        return if (hasTypePrefix()) ids[1].text else ids[0].text
    }

    override fun getDefineTypeIdentifier(): PsiElement? {
        if (!isDefine() || !hasTypePrefix()) return null
        return valueIdentifiers().firstOrNull()?.psi
    }

    override fun getDefineValue(): String? {
        if (!isDefine()) return null
        val valueNode = (this as IsppDirective).value ?: return null
        val ids = valueIdentifiers()
        val nameNode = (if (hasTypePrefix()) ids.getOrNull(1) else ids.getOrNull(0))
            ?: return null

        val nameEndInValue = nameNode.startOffset - valueNode.textRange.startOffset + nameNode.textLength
        val rawAfterName = valueNode.text.substring(nameEndInValue).trimStart()

        if (rawAfterName.isEmpty()) return null
        if (rawAfterName.startsWith('(')) return null  // function-like macro
        return rawAfterName.removeSurrounding("\"").ifEmpty { null }
    }

    // ── PsiNameIdentifierOwner ────────────────────────────────────────────────

    override fun getName(): String? = getDefineName()

    override fun setName(name: String): PsiElement {
        val oldId = getNameIdentifier() ?: return this
        val injManager = InjectedLanguageManager.getInstance(project)
        val hostRange = injManager.injectedToHost(oldId, oldId.textRange)
        val hostFile  = injManager.getTopLevelFile(containingFile)
        val docManager = PsiDocumentManager.getInstance(project)
        val doc = docManager.getDocument(hostFile) ?: return this
        // Commit pending PSI→document operations so the document is no longer locked.
        docManager.doPostponedOperationsAndUnblockDocument(doc)
        doc.replaceString(hostRange.startOffset, hostRange.endOffset, name)
        docManager.commitDocument(doc)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        if (!isDefine()) return null
        val ids = valueIdentifiers()
        if (ids.isEmpty()) return null
        return if (hasTypePrefix()) ids[1].psi else ids[0].psi
    }

    override fun getTextOffset(): Int = getNameIdentifier()?.textOffset ?: super.getTextOffset()
}

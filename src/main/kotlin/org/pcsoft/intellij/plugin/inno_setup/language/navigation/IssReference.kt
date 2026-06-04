package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.issFile
import org.pcsoft.intellij.plugin.inno_setup.language.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.language.valueUnquoted
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

// Anchor is IssParamValue; range points to the specific identifier token within it.
class IssReference(paramValue: IssParamValue, range: TextRange, private val targetSection: String)
    : PsiReferenceBase<IssParamValue>(paramValue, range) {

    override fun resolve(): PsiElement? {
        val file = element.issFile() ?: return null
        val name = element.text.substring(rangeInElement.startOffset, rangeInElement.endOffset)
        return file.findSections(targetSection)
            .flatMap { it.nameDeclarations() }
            .firstOrNull { it.valueUnquoted().equals(name, ignoreCase = true) }
            ?.paramValue
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true
        // element might be the IssParamPair (the PsiNameIdentifierOwner) containing the resolved paramValue
        val resolvedPair = resolved.parent as? IssParamPair
        return resolvedPair != null && mgr.areElementsEquivalent(resolvedPair, element)
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val idNode = element.node.getChildren(TokenSet.create(IssTypes.IDENTIFIER))
            .firstOrNull { it.startOffset - element.textOffset == rangeInElement.startOffset }
            ?: return element
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "[Tasks]\nName: $newElementName\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IssParamValue::class.java)
            ?.node?.findChildByType(IssTypes.IDENTIFIER)?.psi ?: return element
        idNode.psi.replace(newId)
        return element
    }

    override fun getVariants(): Array<Any> {
        val file = element.issFile() ?: return emptyArray()
        return file.findSections(targetSection)
            .flatMap { it.nameDeclarations() }
            .mapNotNull { it.valueUnquoted().ifEmpty { null } }
            .map { LookupElementBuilder.create(it) }
            .toTypedArray()
    }
}

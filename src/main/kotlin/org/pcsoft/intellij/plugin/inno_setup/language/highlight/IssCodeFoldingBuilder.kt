package org.pcsoft.intellij.plugin.inno_setup.language.highlight

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.*
import org.pcsoft.intellij.plugin.inno_setup.language.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService

class IssCodeFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(
        root: PsiElement, document: Document, quick: Boolean
    ): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()

        PsiTreeUtil.findChildrenOfType(root, IssSection::class.java).forEach { section ->
            sectionFold(section)?.let { descriptors += it }

            section.parameterEntryList.forEach { entry ->
                entryFold(entry)?.let { descriptors += it }
            }
        }
        return descriptors.toTypedArray()
    }

    private fun sectionFold(section: IssSection): FoldingDescriptor? {
        val foldStart = section.sectionHeader.textRange.endOffset
        val lastMeaningful = section.children.lastOrNull {
            it is IssDirectiveEntry
            || it is IssParameterEntry
            || it.node?.elementType == IssTypes.COMMENT
        }
        val foldEnd = lastMeaningful?.textRange?.endOffset ?: return null
        if (foldStart >= foldEnd) return null
        return FoldingDescriptor(section.node, TextRange(foldStart, foldEnd))
    }

    private fun entryFold(entry: IssParameterEntry): FoldingDescriptor? {
        val pairs = entry.paramPairList
        if (pairs.size <= 1) return null

        val shownPairs  = resolveShownPairs(entry, pairs)
        val hiddenPairs = pairs - shownPairs.toSet()
        if (hiddenPairs.isEmpty()) return null

        val foldStart = shownPairs.maxOf { it.textRange.endOffset }
        val crlfStart = entry.node.lastChildNode
            ?.takeIf { it.elementType == IssTypes.CRLF }
            ?.startOffset ?: entry.textRange.endOffset
        if (foldStart >= crlfStart) return null

        return FoldingDescriptor(entry.node, TextRange(foldStart, crlfStart))
    }

    private fun resolveShownPairs(
        entry: IssParameterEntry, pairs: List<IssParamPair>
    ): List<IssParamPair> {
        val sectionName = entry.containingSection()?.nameText() ?: return listOf(pairs.first())
        val spec = service<IssSpecService>().spec
        val requiredKeys = spec.sections
            .firstOrNull { it.name.equals(sectionName, ignoreCase = true) }
            ?.attributes
            ?.filter { it.required }
            ?.map { it.name.lowercase() }
            ?.toSet()
            .orEmpty()

        val shownByRequired = if (requiredKeys.isNotEmpty())
            pairs.filter { it.keyText().lowercase() in requiredKeys }
        else emptyList()

        return shownByRequired.ifEmpty { listOf(pairs.first()) }
    }

    override fun getPlaceholderText(node: ASTNode): String = when (node.psi) {
        is IssSection        -> ""
        is IssParameterEntry -> "; ..."
        else                 -> "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean =
        node.elementType == IssTypes.PARAMETER_ENTRY
}

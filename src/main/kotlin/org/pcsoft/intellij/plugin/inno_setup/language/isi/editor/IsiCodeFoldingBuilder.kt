/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.isi.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import org.pcsoft.intellij.plugin.inno_setup.language.isl.specTarget
import org.pcsoft.intellij.plugin.inno_setup.types.appliesTo
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService

class IsiCodeFoldingBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(
        root: PsiElement, document: Document, quick: Boolean
    ): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()

        PsiTreeUtil.findChildrenOfType(root, IsiSection::class.java).forEach { section ->
            sectionFold(section)?.let { descriptors += it }

            section.parameterEntryList.forEach { entry ->
                entryFold(entry)?.let { descriptors += it }
            }
        }
        return descriptors.toTypedArray()
    }

    private fun sectionFold(section: IsiSection): FoldingDescriptor? {
        val foldStart = section.sectionHeader.textRange.endOffset
        val lastMeaningful = section.children.lastOrNull {
            it is IsiDirectiveEntry
                    || it is IsiParameterEntry
                    || it.node?.elementType == IsiTypes.COMMENT
        }
        val foldEnd = lastMeaningful?.textRange?.endOffset ?: return null

        if (foldStart >= foldEnd) return null

        return FoldingDescriptor(section.node, TextRange(foldStart, foldEnd - 1))
    }

    private fun entryFold(entry: IsiParameterEntry): FoldingDescriptor? {
        val pairs = entry.paramPairList
        if (pairs.size <= 1) return null

        val shownPairs = resolveShownPairs(entry, pairs)
        val hiddenPairs = pairs - shownPairs.toSet()
        if (hiddenPairs.isEmpty()) return null

        val foldStart = shownPairs.maxOf { it.textRange.endOffset }
        val crlfStart = (entry.node.lastChildNode
            ?.takeIf { it.elementType == IsiTypes.CRLF }
            ?.startOffset ?: entry.textRange.endOffset)
        if (foldStart >= crlfStart) return null

        return FoldingDescriptor(entry.node, TextRange(foldStart, crlfStart))
    }

    private fun resolveShownPairs(
        entry: IsiParameterEntry, pairs: List<IsiParamPair>
    ): List<IsiParamPair> {
        val sectionName = entry.containingSection?.nameText ?: return listOf(pairs.first())
        val spec = service<IssSpecService>().spec
        val requiredKeys = spec.sections
            .firstOrNull { it.name.equals(sectionName, ignoreCase = true) }
            ?.attributes
            ?.filter { it.required.appliesTo(entry.specTarget) }
            ?.map { it.name.lowercase() }
            ?.toSet()
            .orEmpty()

        val shownByRequired = if (requiredKeys.isNotEmpty())
            pairs.filter { it.keyText().lowercase() in requiredKeys }
        else emptyList()

        return shownByRequired.ifEmpty { listOf(pairs.first()) }
    }

    override fun getPlaceholderText(node: ASTNode): String = when (node.psi) {
        is IsiSection -> ""
        is IsiParameterEntry -> "; ..."
        else -> "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean =
        node.elementType == IsiTypes.PARAMETER_ENTRY
}

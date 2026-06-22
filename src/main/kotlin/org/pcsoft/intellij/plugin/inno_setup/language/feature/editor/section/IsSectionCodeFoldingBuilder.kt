/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorConditionalStructure
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.hostRange
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.sectionAtOffset
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.appliesTo

/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
class IsSectionCodeFoldingBuilder : FoldingBuilderEx() {

    /**
     * Returns code-folding metadata for Inno Setup sections.
     */
    override fun buildFoldRegions(
        root: PsiElement, document: Document, quick: Boolean
    ): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()

        PsiTreeUtil.findChildrenOfType(root, IsSectionBlock::class.java).forEach { section ->
            sectionFold(section)?.let { descriptors += it }

            section.parameterEntryList.forEach { entry ->
                entryFold(entry)?.let { descriptors += it }
            }
        }

        if (root is IsScriptFile) {
            conditionalFolds(root).forEach { descriptors += it }
        }
        return descriptors.toTypedArray()
    }

    /**
     * Folds for `#if … #endif` blocks — only when the whole block lies within a single section or entirely
     * outside any section (it must not cross a section header). Two offsets share a section iff
     * [sectionAtOffset] returns the same block (or `null` for both), which also rules out a crossing header.
     */
    private fun conditionalFolds(file: IsScriptFile): List<FoldingDescriptor> {
        return IsPreprocessorConditionalStructure.structureOf(file).blocks.mapNotNull { block ->
            val range = block.hostRange
            if (file.sectionAtOffset(range.startOffset) !== file.sectionAtOffset(range.endOffset)) return@mapNotNull null
            if (range.startOffset >= range.endOffset) return@mapNotNull null
            FoldingDescriptor(block.openerLine.node, range)
        }
    }

    private fun sectionFold(section: IsSectionBlock): FoldingDescriptor? {
        val foldStart = section.header.textRange.endOffset
        val lastMeaningful = section.children.lastOrNull {
            it is IsSectionDirectiveEntry
                    || it is IsSectionParameterEntry
                    || it.node?.elementType == IsSectionTypes.COMMENT
        }
        val foldEnd = lastMeaningful?.textRange?.endOffset ?: return null

        if (foldStart >= foldEnd) return null

        return FoldingDescriptor(section.node, TextRange(foldStart, foldEnd - 1))
    }

    private fun entryFold(entry: IsSectionParameterEntry): FoldingDescriptor? {
        val pairs = entry.paramPairList
        if (pairs.size <= 1) return null

        val shownPairs = resolveShownPairs(entry, pairs)
        val hiddenPairs = pairs - shownPairs.toSet()
        if (hiddenPairs.isEmpty()) return null

        val foldStart = shownPairs.maxOf { it.textRange.endOffset }
        val crlfStart = (entry.node.lastChildNode
            ?.takeIf { it.elementType == IsSectionTypes.CRLF }
            ?.startOffset ?: entry.textRange.endOffset)
        if (foldStart >= crlfStart) return null

        return FoldingDescriptor(entry.node, TextRange(foldStart, crlfStart))
    }

    private fun resolveShownPairs(
        entry: IsSectionParameterEntry, pairs: List<IsSectionParamPair>
    ): List<IsSectionParamPair> {
        val name = entry.containingSection?.nameText ?: return listOf(pairs.first())
        val spec = service<IsSpecService>().spec
        val requiredKeys = spec.sections
            .firstOrNull { it.name.equals(name, ignoreCase = true) }
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

    /**
     * Returns code-folding metadata for Inno Setup sections.
     */
    override fun getPlaceholderText(node: ASTNode): String = when (val psi = node.psi) {
        is IsSectionBlock -> ""
        is IsSectionParameterEntry -> "; ..."
        is IsSectionPreprocessorLine -> psi.text.trim().ifEmpty { "#if ..." } + " ... #endif"
        else -> "..."
    }

    /**
     * Returns code-folding metadata for Inno Setup sections.
     */
    override fun isCollapsedByDefault(node: ASTNode): Boolean =
        node.elementType == IsSectionTypes.PARAMETER_ENTRY
}

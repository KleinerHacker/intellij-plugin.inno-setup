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

package org.pcsoft.intellij.plugin.inno_setup.script.language.feature.editor.section

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsConditionalBranch
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorConditionalStructure
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSubroutineStructure
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.hostRange
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.sectionAtOffset

/** A preprocessor block (`#if … #endif`, `#sub … #endsub`) reduced to its host line, range and branches. */
data class IsPreprocessorBlockRange(
    /** The host preprocessor line carrying the opener (`#if`/`#sub`/…). */
    val openerLine: PsiElement,
    /** The host range from the opener line start to the closing line end. */
    val range: TextRange,
    /** The branches of a conditional block (opener, `#elif`s, `#else`); empty for a `#sub` block. */
    val branches: List<IsConditionalBranch> = emptyList(),
)

/**
 * The matched `#if … #endif` and `#sub … #endsub` blocks of a host file, reduced to plain host ranges and
 * cached per file. Used by the folding builder to build its fold regions.
 *
 * Only blocks that lie within a single section (or entirely outside any section) are reported: two offsets
 * share a section iff [sectionAtOffset] returns the same block (or `null` for both), which also rules out a
 * block crossing a section header.
 */
object IsPreprocessorBlockRanges {

    /** All foldable blocks of [file], each spanning from its opener line to its closing line. */
    fun blocksOf(file: IsScriptFile): List<IsPreprocessorBlockRange> =
        CachedValuesManager.getCachedValue(file) {
            CachedValueProvider.Result.create(compute(file), file)
        }

    private fun compute(file: IsScriptFile): List<IsPreprocessorBlockRange> {
        val conditionals = IsPreprocessorConditionalStructure.structureOf(file).blocks
            .map { IsPreprocessorBlockRange(it.openerLine, it.hostRange, it.branches) }
        val subroutines = IsPreprocessorSubroutineStructure.structureOf(file).blocks
            .map { IsPreprocessorBlockRange(it.openerLine, it.hostRange) }

        return (conditionals + subroutines).filter { block ->
            val range = block.range
            file.sectionAtOffset(range.startOffset) === file.sectionAtOffset(range.endOffset)
                    && range.startOffset < range.endOffset
        }
    }
}

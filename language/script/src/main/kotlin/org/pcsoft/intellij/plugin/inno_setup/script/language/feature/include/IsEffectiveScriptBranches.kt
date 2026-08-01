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

package org.pcsoft.intellij.plugin.inno_setup.script.language.feature.include

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorBranchAnalysis

/**
 * Second phase of building an effective script: after `#include` inlining has produced one flat text, the
 * conditional-compilation result is applied to it.
 *
 * Deliberately a separate pass over the already-inlined text rather than something woven into
 * [mergeIncludes]. Include resolution is textual and file-recursive, branch evaluation is PSI- and
 * order-driven; keeping them apart means `#define`s contributed by an included file are simply *there* when
 * the conditions are evaluated, with no cross-file bookkeeping.
 *
 * What a fully decided block leaves behind is only the body of its active branch — the `#if`/`#elif`/`#else`/
 * `#endif` lines and every skipped body are dropped. A block containing an undecidable branch is emitted
 * unchanged, preceded by a single comment line, because which of its branches survives is exactly what could
 * not be determined.
 */
internal object IsEffectiveScriptBranches {

    /** Marker written above a block whose condition could not be decided. English, like all console output. */
    const val UNDECIDED_COMMENT = "; Condition not statically evaluable - both branches kept"

    /**
     * Applies the branch analysis of [analysed] to [text] and returns the pruned text together with the
     * source-map [segments] adjusted to it. [analysed] must be the PSI file [text] was parsed into, so the
     * analysis offsets and the text line up.
     */
    fun prune(text: String, segments: List<IsEffectiveSegment>, analysed: PsiFile): Pair<String, List<IsEffectiveSegment>> {
        val analysis = IsPreprocessorBranchAnalysis.analyze(analysed)
        val removals = merge(
            (analysis.inactiveRanges + analysis.decidedDirectiveLines).map { it.withTrailingNewline(text) }
        )
        val insertions = analysis.undecidedBlockOffsets.toSortedSet()
        if (removals.isEmpty() && insertions.isEmpty()) return text to segments

        val builder = StringBuilder()
        val result = mutableListOf<IsEffectiveSegment>()

        /** Copies `[from, to)` of the original text, splitting it across the segments it spans. */
        fun copy(from: Int, to: Int) {
            if (to <= from) return
            segments.forEach { segment ->
                val start = maxOf(segment.range.startOffset, from)
                val end = minOf(segment.range.endOffset, to)
                if (end <= start) return@forEach
                val at = builder.length
                builder.append(text, start, end)
                result += IsEffectiveSegment(
                    range = TextRange(at, builder.length),
                    origin = segment.origin,
                    sourceStart = segment.sourceStart + (start - segment.range.startOffset),
                )
            }
        }

        var cursor = 0
        // Both lists are sorted, so one forward walk emits the kept text and the markers in order.
        (removals.map { it.startOffset to it } + insertions.map { it to null })
            .sortedBy { it.first }
            .forEach { (offset, removal) ->
                if (offset > cursor) copy(cursor, offset)
                if (removal == null) {
                    // A marker is plain generated text and belongs to no source file, so it gets no segment;
                    // an offset inside it simply has no origin, exactly like unattributed host text.
                    builder.append(UNDECIDED_COMMENT).append('\n')
                } else {
                    cursor = maxOf(cursor, removal.endOffset)
                }
            }
        copy(cursor, text.length)

        return builder.toString() to result
    }

    /** Extends a line range over its line terminator, so removing it does not leave a blank line behind. */
    private fun TextRange.withTrailingNewline(text: String): TextRange {
        var end = endOffset
        if (end < text.length && text[end] == '\r') end++
        if (end < text.length && text[end] == '\n') end++
        return TextRange(startOffset, end)
    }

    /** Sorts and merges overlapping/adjacent ranges so the forward walk never sees them nested. */
    private fun merge(ranges: List<TextRange>): List<TextRange> {
        if (ranges.isEmpty()) return emptyList()
        val sorted = ranges.sortedBy { it.startOffset }
        val merged = mutableListOf(sorted.first())
        sorted.drop(1).forEach { range ->
            val last = merged.last()
            if (range.startOffset <= last.endOffset) {
                merged[merged.lastIndex] = TextRange(last.startOffset, maxOf(last.endOffset, range.endOffset))
            } else {
                merged += range
            }
        }
        return merged
    }
}

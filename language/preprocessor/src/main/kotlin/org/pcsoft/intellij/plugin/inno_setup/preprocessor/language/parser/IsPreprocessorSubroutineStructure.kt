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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx

/** Why a subroutine directive (`#sub`/`#endsub`) is structurally invalid. */
enum class IsSubroutineProblem {
    /** A `#sub` that is never closed by an `#endsub` before EOF. */
    UnterminatedSub,

    /** An `#endsub` that has no matching open `#sub`. */
    StrayEndsub,
}

/** A matched `#sub … #endsub` block at host-file level (used for folding). */
data class IsSubroutineBlock(
    /** The host preprocessor line element carrying the opening `#sub`. */
    val openerLine: PsiElement,
    /** The host preprocessor line element carrying the closing `#endsub`. */
    val endsubLine: PsiElement,
)

/** Result of analysing the subroutine structure of a host file. */
data class IsSubroutineStructure(
    /** Matched `#sub`→`#endsub` blocks (including nested ones), in host order. */
    val blocks: List<IsSubroutineBlock>,
    /** Each structurally invalid directive together with the reason. */
    val problems: Map<IsPreprocessorDirective, IsSubroutineProblem>,
)

/**
 * Analyses the `#sub`/`#endsub` structure of an ISPP host file: matches each `#sub` to its `#endsub` and
 * flags unterminated `#sub`s and stray `#endsub`s. Shared by the annotator (per-directive validity) and the
 * folding builder (block ranges) — mirroring [IsPreprocessorConditionalStructure].
 */
object IsPreprocessorSubroutineStructure {

    private class Open(val opener: IsPreprocessorDirective, val openerLine: PsiElement)

    fun structureOf(hostFile: PsiFile): IsSubroutineStructure {
        val stack = ArrayDeque<Open>()
        val blocks = mutableListOf<IsSubroutineBlock>()
        val problems = mutableMapOf<IsPreprocessorDirective, IsSubroutineProblem>()

        for ((directive, line) in preprocessorDirectivesWithHostLine(hostFile)) {
            val ex = directive as? IsPreprocessorDirectiveEx ?: continue
            when {
                ex.isSub() -> stack.addLast(Open(directive, line))
                ex.isEndsub() -> {
                    val open = stack.removeLastOrNull()
                    if (open == null) problems[directive] = IsSubroutineProblem.StrayEndsub
                    else blocks += IsSubroutineBlock(open.openerLine, line)
                }
            }
        }

        // Anything still open at EOF is an unterminated #sub.
        stack.forEach { problems[it.opener] = IsSubroutineProblem.UnterminatedSub }

        return IsSubroutineStructure(blocks, problems)
    }
}

/** The host text range spanned by a subroutine block, from the `#sub` line start to the `#endsub` line end. */
val IsSubroutineBlock.hostRange: TextRange
    get() = TextRange(openerLine.textRange.startOffset, endsubLine.textRange.endOffset)

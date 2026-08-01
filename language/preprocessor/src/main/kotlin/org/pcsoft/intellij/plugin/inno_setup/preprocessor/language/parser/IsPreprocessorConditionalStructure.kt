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

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx

/** Boolean words that ISPP does not actually support (they degrade to undefined identifiers = 0). */
val IS_PREPROCESSOR_BOOLEAN_WORDS = setOf("true", "false", "yes", "no")

/** Why a conditional directive is structurally invalid. */
enum class IsConditionalProblem {
    /** A `#if`/`#ifdef`/… opener that is never closed by an `#endif` before EOF. */
    UnterminatedOpener,

    /** An `#elif`/`#else`/`#endif` that has no matching open conditional block. */
    StrayBranch,

    /** An `#elif` that appears after the block's `#else`. */
    ElifAfterElse,
}

/** One branch of a conditional block: its opener (`#if`/`#ifdef`/…), an `#elif` or the `#else`. */
data class IsConditionalBranch(
    /** The host preprocessor line element carrying the branch directive. */
    val line: PsiElement,
    /** Whether this branch is the block's `#else` (as opposed to the opener or an `#elif`). */
    val isElse: Boolean,
)

/** A matched `#if … #endif` block at host-file level (used for folding and sticky lines). */
data class IsConditionalBlock(
    /** The host preprocessor line element carrying the opener (`#if`/`#ifdef`/…). */
    val openerLine: PsiElement,
    /** The host preprocessor line element carrying the closing `#endif`. */
    val endifLine: PsiElement,
    /** The block's branches in host order: the opener first, then each `#elif`, then the `#else`. */
    val branches: List<IsConditionalBranch>,
)

/** Result of analysing the conditional structure of a host file. */
data class IsConditionalStructure(
    /** Matched opener→`#endif` blocks (including nested ones), in host order. */
    val blocks: List<IsConditionalBlock>,
    /** Each structurally invalid directive together with the reason. */
    val problems: Map<IsPreprocessorDirective, IsConditionalProblem>,
)

/**
 * Analyses the `#if`/`#elif`/`#else`/`#endif` (and `#ifdef`/`#ifndef`/`#ifexist`/`#ifnexist`) structure of
 * an ISPP host file: matches openers to their `#endif`, records the branches of each block, and flags
 * unterminated openers, stray branches and `#elif`-after-`#else`. Shared by the annotator (per-directive
 * validity), the folding builder (ranges) and the sticky lines (per-branch ranges).
 */
object IsPreprocessorConditionalStructure {

    private class Open(
        val opener: IsPreprocessorDirective,
        val openerLine: PsiElement,
        var sawElse: Boolean = false,
    ) {
        val branches = mutableListOf(IsConditionalBranch(openerLine, isElse = false))
    }

    fun structureOf(hostFile: PsiFile): IsConditionalStructure =
        structureOf(preprocessorDirectivesWithHostLine(hostFile))

    /**
     * Same analysis over an already-collected directive list.
     *
     * Collecting it means enumerating the ISPP injection of every preprocessor line, which is the dominant
     * cost on a large script — doing it twice for one analysis made the branch evaluation scale far worse
     * than the number of directives alone would suggest. Callers that already hold the list pass it in.
     */
    fun structureOf(directivesWithLine: List<Pair<IsPreprocessorDirective, PsiElement>>): IsConditionalStructure {
        val stack = ArrayDeque<Open>()
        val blocks = mutableListOf<IsConditionalBlock>()
        val problems = mutableMapOf<IsPreprocessorDirective, IsConditionalProblem>()

        for ((directive, line) in directivesWithLine) {
            val ex = directive as? IsPreprocessorDirectiveEx ?: continue
            when {
                ex.isConditionalOpener() -> stack.addLast(Open(directive, line))

                ex.isElif() -> {
                    val open = stack.lastOrNull()
                    if (open == null) problems[directive] = IsConditionalProblem.StrayBranch
                    else if (open.sawElse) problems[directive] = IsConditionalProblem.ElifAfterElse
                    else open.branches += IsConditionalBranch(line, isElse = false)
                }

                ex.isElse() -> {
                    val open = stack.lastOrNull()
                    if (open == null) problems[directive] = IsConditionalProblem.StrayBranch
                    else if (open.sawElse) problems[directive] = IsConditionalProblem.StrayBranch
                    else {
                        open.sawElse = true
                        open.branches += IsConditionalBranch(line, isElse = true)
                    }
                }

                ex.isEndif() -> {
                    val open = stack.removeLastOrNull()
                    if (open == null) problems[directive] = IsConditionalProblem.StrayBranch
                    else blocks += IsConditionalBlock(open.openerLine, line, open.branches.toList())
                }
            }
        }

        // Anything still open at EOF is an unterminated opener.
        stack.forEach { problems[it.opener] = IsConditionalProblem.UnterminatedOpener }

        return IsConditionalStructure(blocks, problems)
    }
}

/** The ISPP directives of [hostFile] paired with the host preprocessor line they live on, in document order. */
fun preprocessorDirectivesWithHostLine(hostFile: PsiFile): List<Pair<IsPreprocessorDirective, PsiElement>> {
    val mgr = InjectedLanguageManager.getInstance(hostFile.project)
    return hostFile.children
        .filter { it is IsPreprocessorHostLine }
        .flatMap { line ->
            val result = mutableListOf<Pair<IsPreprocessorDirective, PsiElement>>()
            mgr.enumerate(line) { injectedPsi, _ ->
                if (injectedPsi is IsPreprocessorFile) {
                    PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsPreprocessorDirective::class.java)
                        .forEach { result += it to line }
                }
            }
            result
        }
}

/** The host text range spanned by a conditional block, from the opener line start to the `#endif` line end. */
val IsConditionalBlock.hostRange: TextRange
    get() = TextRange(openerLine.textRange.startOffset, endifLine.textRange.endOffset)

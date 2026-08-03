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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.preprocessor

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorBranchAnalysis
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSymbolTracker
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Guards the cost of [IsPreprocessorBranchAnalysis] **without measuring time**.
 *
 * The analysis walks injected PSI and runs on every highlighting pass, so a lost cache or a redundant
 * re-evaluation would show up as typing lag. Wall-clock assertions are flaky on shared CI machines; counting
 * the work instead is exact and reproducible, which is why these are developer tests while the timing-based
 * checks are integration tests (`…PerformanceIT`).
 *
 * The scripts are added to the project rather than opened in the editor: an open file is continuously
 * re-analysed by the highlighting daemon, which would warm the cache underneath the measurement and make
 * every count racy.
 */
class IsPreprocessorBranchAnalysisCacheTest : IsTimedBasePlatformTestCase() {

    private var counter = 0

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    /** An `.iss` file in the project but not open in an editor, so nothing analyses it behind our back. */
    private fun script(content: String): PsiFile =
        myFixture.addFileToProject("branch${counter++}.iss", content + setupTail)

    /** [blocks] independent, trivially true `#if` blocks. */
    private fun blocks(blocks: Int) = (1..blocks).joinToString("") { "#if $it\n; body $it\n#endif\n" }

    /** Conditions evaluated while running [body]. */
    private fun conditionsEvaluatedBy(body: () -> Unit): Long {
        val before = IsPreprocessorBranchAnalysis.evaluatedConditions.get()
        body()
        return IsPreprocessorBranchAnalysis.evaluatedConditions.get() - before
    }

    // ── the cache actually caches ──────────────────────────────────────────────

    fun testRepeatedAnalysisDoesNoWork() {
        val file = script(blocks(5))
        IsPreprocessorBranchAnalysis.analyze(file)

        val work = conditionsEvaluatedBy { repeat(10) { IsPreprocessorBranchAnalysis.analyze(file) } }
        assertEquals("A cached analysis must not re-evaluate any condition", 0L, work)
    }

    fun testRepeatedAnalysisReturnsTheSameInstance() {
        val file = script(blocks(3))
        assertSame(
            "The cache must hand out the identical result, not an equal copy",
            IsPreprocessorBranchAnalysis.analyze(file),
            IsPreprocessorBranchAnalysis.analyze(file),
        )
    }

    fun testAnalysisIsRecomputedAfterAnEdit() {
        val file = script(blocks(3))
        val before = IsPreprocessorBranchAnalysis.analyze(file)

        val document = PsiDocumentManager.getInstance(project).getDocument(file)!!
        WriteCommandAction.runWriteCommandAction(project) {
            document.insertString(0, "; edit\n")
            PsiDocumentManager.getInstance(project).commitDocument(document)
        }

        assertNotSame(
            "An edit must invalidate the cached analysis",
            before,
            IsPreprocessorBranchAnalysis.analyze(file),
        )
    }

    /**
     * Symbols contributed from outside the script — the `/D…` of the selected build configuration — change
     * without any edit, so bumping [IsPreprocessorSymbolTracker] must invalidate the cached analysis.
     * Otherwise switching the build configuration would leave the greyed-out branches on the old outcome
     * until the file is typed in.
     */
    fun testAnalysisIsRecomputedAfterExternalSymbolsChanged() {
        val file = script(blocks(3))
        val before = IsPreprocessorBranchAnalysis.analyze(file)

        IsPreprocessorSymbolTracker.getInstance(project).symbolsChanged()

        assertNotSame(
            "Changed external symbols must invalidate the cached analysis",
            before,
            IsPreprocessorBranchAnalysis.analyze(file),
        )
    }

    /**
     * The counterpart: as long as no external symbol source reports a change, the added dependency must not
     * cost anything — the cache still has to hand out the identical result.
     */
    fun testUnchangedExternalSymbolsKeepTheCachedAnalysis() {
        val file = script(blocks(3))
        val before = IsPreprocessorBranchAnalysis.analyze(file)

        assertSame(
            "An unchanged symbol tracker must not invalidate the cache",
            before,
            IsPreprocessorBranchAnalysis.analyze(file),
        )
    }

    // ── amount of work per analysis ────────────────────────────────────────────

    fun testEachConditionIsEvaluatedExactlyOnce() {
        val file = script(blocks(7))
        val work = conditionsEvaluatedBy { IsPreprocessorBranchAnalysis.analyze(file) }
        assertEquals("Every condition must be evaluated exactly once per analysis", 7L, work)
    }

    /** Nesting must not make an inner condition be walked once per enclosing branch. */
    fun testNestedConditionsAreNotEvaluatedRepeatedly() {
        val file = script("#if 1\n#if 1\n#if 1\n; deep\n#endif\n#endif\n#endif\n")
        val work = conditionsEvaluatedBy { IsPreprocessorBranchAnalysis.analyze(file) }
        assertEquals("Three nested conditions means three evaluations", 3L, work)
    }

    /** A skipped branch's nested conditions are inherited, never evaluated — the real preprocessor agrees. */
    fun testConditionsInsideDeadBranchesAreNotEvaluated() {
        val file = script("#if 0\n#if 1\n; dead\n#endif\n#endif\n")
        val work = conditionsEvaluatedBy { IsPreprocessorBranchAnalysis.analyze(file) }
        assertEquals("A dead branch's nested condition is inherited, not evaluated", 1L, work)
    }

    /**
     * The guard against the quadratic trap: the analysis must not re-scan the whole directive list per
     * directive. Quadrupling the block count may quadruple the evaluations, never square them.
     */
    fun testWorkGrowsLinearlyWithBlockCount() {
        val small = conditionsEvaluatedBy { IsPreprocessorBranchAnalysis.analyze(script(blocks(10))) }
        val large = conditionsEvaluatedBy { IsPreprocessorBranchAnalysis.analyze(script(blocks(40))) }
        assertEquals("Evaluations must scale one-to-one with conditions", 10L, small)
        assertEquals("Evaluations must scale one-to-one with conditions", 40L, large)
    }
}

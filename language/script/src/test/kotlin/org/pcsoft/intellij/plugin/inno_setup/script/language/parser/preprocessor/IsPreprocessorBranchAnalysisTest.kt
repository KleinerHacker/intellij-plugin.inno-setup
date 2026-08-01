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

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsBranchState
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorBranchAnalysis
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Tests for [IsPreprocessorBranchAnalysis]: which `#if` branches are provably compiled, provably skipped, or
 * undecidable. The bias under test is deliberately asymmetric — wrongly reporting
 * [IsBranchState.INACTIVE] greys out live code and is the expensive failure, so every case that the analysis
 * cannot prove must come out as [IsBranchState.UNKNOWN].
 */
class IsPreprocessorBranchAnalysisTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    /**
     * Configures [text] as an `.iss` file and forces a full highlighting pass, which is what materialises the
     * ISPP language injection the analysis walks (without it the host file has no injected directives yet).
     *
     * Returns the **host** script: with the caret at offset 0 — inside the first `#…` line — `myFixture.file`
     * is the injected ISPP fragment, whose text and offsets are not the ones the analysis reports in.
     */
    private fun analyze(text: String): PsiFile {
        myFixture.configureByText(IsScriptFileType.INSTANCE, text + setupTail)
        myFixture.doHighlighting()
        return InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file)
    }

    /** Branch states of [text] in document order — one entry per `#if`/`#elif`/`#else` line. */
    private fun statesOf(text: String): List<IsBranchState> {
        val analysis = IsPreprocessorBranchAnalysis.analyze(analyze(text))
        return analysis.states.entries.sortedBy { it.key }.map { it.value }
    }

    private fun assertStates(text: String, vararg expected: IsBranchState) {
        val actual = statesOf(text)
        assertEquals("Branch states of:\n$text", expected.toList(), actual)
    }

    // ── literal conditions ─────────────────────────────────────────────────────

    fun testLiteralTrueIsActive() =
        assertStates("#if 1\n#endif\n", IsBranchState.ACTIVE)

    fun testLiteralFalseIsInactive() =
        assertStates("#if 0\n#endif\n", IsBranchState.INACTIVE)

    fun testArithmeticConditionIsDecided() =
        assertStates("#if 2 > 1\n#endif\n", IsBranchState.ACTIVE)

    // ── #define driven conditions ──────────────────────────────────────────────

    fun testDefineValueDecidesCondition() =
        assertStates("#define A 1\n#if A\n#endif\n", IsBranchState.ACTIVE)

    fun testDefineValueDecidesConditionFalse() =
        assertStates("#define A 0\n#if A\n#endif\n", IsBranchState.INACTIVE)

    fun testTransitiveDefineIsResolved() =
        assertStates("#define A 10\n#define B A + 5\n#if B == 15\n#endif\n", IsBranchState.ACTIVE)

    fun testUndefinedIdentifierEvaluatesToZero() =
        assertStates("#if Nope\n#endif\n", IsBranchState.INACTIVE)

    // ── #ifdef / #ifndef ───────────────────────────────────────────────────────

    fun testIfdefOnDefinedSymbol() =
        assertStates("#define A\n#ifdef A\n#endif\n", IsBranchState.ACTIVE)

    fun testIfdefOnUndefinedSymbol() =
        assertStates("#ifdef A\n#endif\n", IsBranchState.INACTIVE)

    fun testIfndefInvertsIfdef() =
        assertStates("#ifndef A\n#endif\n", IsBranchState.ACTIVE)

    fun testUndefRemovesSymbol() =
        assertStates("#define A 1\n#undef A\n#ifdef A\n#endif\n", IsBranchState.INACTIVE)

    fun testDefinedCallIsResolved() =
        assertStates("#define A 1\n#if defined(A)\n#endif\n", IsBranchState.ACTIVE)

    fun testDefinedCallOnUnknownSymbolIsFalse() =
        assertStates("#if defined(A)\n#endif\n", IsBranchState.INACTIVE)

    /** A valueless predefined symbol exists precisely to be tested with `#ifdef`. */
    fun testPredefinedVoidSymbolCountsAsDefined() =
        assertStates("#ifdef ISPP_INVOKED\n#endif\n", IsBranchState.ACTIVE)

    /** …but its *value* is decided by the compiler, so it cannot drive a value comparison. */
    fun testPredefinedSymbolValueIsUnknown() =
        assertStates("#if PREPROCVER > 1\n#endif\n", IsBranchState.UNKNOWN)

    // ── branch chains ──────────────────────────────────────────────────────────

    fun testElseOfFalseConditionIsActive() =
        assertStates("#if 0\n#else\n#endif\n", IsBranchState.INACTIVE, IsBranchState.ACTIVE)

    fun testElseOfTrueConditionIsInactive() =
        assertStates("#if 1\n#else\n#endif\n", IsBranchState.ACTIVE, IsBranchState.INACTIVE)

    fun testElifChainPicksFirstTrueBranch() =
        assertStates(
            "#if 0\n#elif 1\n#elif 1\n#else\n#endif\n",
            IsBranchState.INACTIVE, IsBranchState.ACTIVE, IsBranchState.INACTIVE, IsBranchState.INACTIVE,
        )

    /** Once a branch is undecidable, no later branch of the same block can be decided either. */
    fun testUnknownBranchMakesLaterBranchesUnknown() =
        assertStates(
            "#if PREPROCVER > 1\n#elif 1\n#else\n#endif\n",
            IsBranchState.UNKNOWN, IsBranchState.UNKNOWN, IsBranchState.UNKNOWN,
        )

    // ── nesting ────────────────────────────────────────────────────────────────

    fun testNestedBlockInsideActiveBranchIsEvaluated() =
        assertStates(
            "#if 1\n#if 0\n#endif\n#endif\n",
            IsBranchState.ACTIVE, IsBranchState.INACTIVE,
        )

    /** A nested condition inside dead code is never evaluated by the real preprocessor either. */
    fun testNestedBlockInsideInactiveBranchIsInactive() =
        assertStates(
            "#if 0\n#if 1\n#endif\n#endif\n",
            IsBranchState.INACTIVE, IsBranchState.INACTIVE,
        )

    fun testNestedBlockInsideUnknownBranchIsUnknown() =
        assertStates(
            "#if PREPROCVER > 1\n#if 1\n#endif\n#endif\n",
            IsBranchState.UNKNOWN, IsBranchState.UNKNOWN,
        )

    // ── environment mutation under conditionals ────────────────────────────────

    /** A `#define` in a skipped branch never happens, so the symbol stays undefined. */
    fun testDefineInsideInactiveBranchIsNotVisible() =
        assertStates(
            "#if 0\n#define A 1\n#endif\n#ifdef A\n#endif\n",
            IsBranchState.INACTIVE, IsBranchState.INACTIVE,
        )

    fun testDefineInsideActiveBranchIsVisible() =
        assertStates(
            "#if 1\n#define A 1\n#endif\n#ifdef A\n#endif\n",
            IsBranchState.ACTIVE, IsBranchState.ACTIVE,
        )

    /** A `#define` in an undecidable branch makes the symbol's very existence undecidable. */
    fun testDefineInsideUnknownBranchPoisonsSymbol() =
        assertStates(
            "#if PREPROCVER > 1\n#define A 1\n#endif\n#ifdef A\n#endif\n",
            IsBranchState.UNKNOWN, IsBranchState.UNKNOWN,
        )

    /** Defined before, possibly redefined inside — still definitely defined, but the value is gone. */
    fun testRedefineInsideUnknownBranchKeepsDefinednessButLosesValue() =
        assertStates(
            "#define A 1\n#if PREPROCVER > 1\n#define A 2\n#endif\n#ifdef A\n#endif\n#if A == 1\n#endif\n",
            IsBranchState.UNKNOWN, IsBranchState.ACTIVE, IsBranchState.UNKNOWN,
        )

    fun testUndefInsideUnknownBranchMakesDefinednessUnknown() =
        assertStates(
            "#define A 1\n#if PREPROCVER > 1\n#undef A\n#endif\n#ifdef A\n#endif\n",
            IsBranchState.UNKNOWN, IsBranchState.UNKNOWN,
        )

    // ── side effects poison the analysis ───────────────────────────────────────

    /** `#expr` can assign to any name, so nothing after it is trusted. */
    fun testExprPoisonsLaterConditions() =
        assertStates(
            "#define A 1\n#expr A\n#if A\n#endif\n",
            IsBranchState.UNKNOWN,
        )

    fun testForLoopPoisonsLaterConditions() =
        assertStates(
            "#define A 1\n#for {i = 0; i < 3; i++} A\n#if A\n#endif\n",
            IsBranchState.UNKNOWN,
        )

    /** An impure builtin reads state that only exists while compiling. */
    fun testImpureBuiltinCallIsUndecidable() =
        assertStates("#if GetEnv(\"PATH\") != \"\"\n#endif\n", IsBranchState.UNKNOWN)

    fun testImpureBuiltinCallPoisonsLaterConditions() =
        assertStates(
            "#define A 1\n#if GetEnv(\"X\") == \"\"\n#endif\n#if A\n#endif\n",
            IsBranchState.UNKNOWN, IsBranchState.UNKNOWN,
        )

    /** A pure builtin the evaluator cannot fold only makes *this* condition undecidable. */
    fun testPureBuiltinCallDoesNotPoisonLaterConditions() =
        assertStates(
            "#define A 1\n#if Len(\"abc\") == 3\n#endif\n#if A\n#endif\n",
            IsBranchState.UNKNOWN, IsBranchState.ACTIVE,
        )

    // ── inactive ranges ────────────────────────────────────────────────────────

    fun testInactiveRangeCoversOnlyTheBranchBody() {
        // A comment keeps the branch body syntactically valid at top level, so the block really closes.
        val file = analyze("#if 0\n; AAAA\n#endif\n")
        val analysis = IsPreprocessorBranchAnalysis.analyze(file)

        assertEquals("Exactly one inactive branch body expected", 1, analysis.inactiveRanges.size)
        val body = analysis.inactiveRanges.single().substring(file.text)
        assertTrue("Inactive range must contain the branch body, was '$body'", body.contains("AAAA"))
        assertFalse("Inactive range must not swallow the #if line", body.contains("#if"))
        assertFalse("Inactive range must not swallow the #endif line", body.contains("#endif"))
    }

    fun testNoInactiveRangeForUndecidableBlock() {
        val file = analyze("#if PREPROCVER > 1\nAAAA\n#else\nBBBB\n#endif\n")
        val analysis = IsPreprocessorBranchAnalysis.analyze(file)
        assertTrue(
            "An undecidable block must keep both branches, got ${analysis.inactiveRanges}",
            analysis.inactiveRanges.isEmpty(),
        )
        assertEquals("The undecidable condition must be reported", 1, analysis.unknown.size)
    }
}

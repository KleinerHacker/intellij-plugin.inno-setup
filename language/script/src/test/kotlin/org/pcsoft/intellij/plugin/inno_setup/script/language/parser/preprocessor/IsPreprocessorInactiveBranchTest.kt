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

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.IsSectionAnnotatorHighlighting
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Editor-facing behaviour of the conditional-branch analysis: dead branches are dimmed, undecidable
 * conditions get a weak-warning marker instead, and dead code stops producing diagnostics.
 */
class IsPreprocessorInactiveBranchTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun highlights(text: String): List<HighlightInfo> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, text + setupTail)
        return myFixture.doHighlighting()
    }

    private fun dimmed(text: String) =
        highlights(text).filter { it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.INACTIVE_BRANCH }

    private fun undecidableMarkers(text: String) = highlights(text)
        .filter { it.severity.name == "WEAK WARNING" }
        .filter { it.description?.contains("cannot be evaluated statically") == true }

    // ── dimming ────────────────────────────────────────────────────────────────

    fun testDeadBranchBodyIsDimmed() {
        val infos = dimmed("#if 0\n; dead\n#endif\n")
        assertEquals("The dead branch body must be dimmed exactly once", 1, infos.size)
    }

    fun testLiveBranchBodyIsNotDimmed() {
        assertTrue("A compiled branch must keep its normal colours", dimmed("#if 1\n; live\n#endif\n").isEmpty())
    }

    fun testUndecidableBranchIsNotDimmed() {
        assertTrue(
            "An undecidable condition must leave both branches lit",
            dimmed("#if PREPROCVER > 1\n; a\n#else\n; b\n#endif\n").isEmpty(),
        )
    }

    fun testElseOfTakenBranchIsDimmed() {
        assertEquals(
            "The #else of a taken branch is dead and must be dimmed",
            1,
            dimmed("#if 1\n; live\n#else\n; dead\n#endif\n").size,
        )
    }

    // ── undecidable marker ─────────────────────────────────────────────────────

    fun testUndecidableConditionIsMarked() {
        val markers = undecidableMarkers("#if PREPROCVER > 1\n#endif\n")
        assertEquals("An undecidable condition must be marked exactly once", 1, markers.size)
        assertTrue(
            "The marker must say both branches are kept, was '${markers.single().description}'",
            markers.single().description!!.contains("both branches are kept"),
        )
    }

    fun testStringComparisonAgainstADefineIsDecided() {
        val text = "#define demo \"test\"\n#if demo == \"test\"\n; live\n#else\n; dead\n#endif\n"
        assertTrue("A string comparison against a known #define is decidable", undecidableMarkers(text).isEmpty())
        assertEquals("The #else of the taken branch must be dimmed", 1, dimmed(text).size)
    }

    fun testIfExistWithLiteralPathIsDecided() {
        myFixture.addFileToProject("target.iss", "; content\n")
        val text = "#ifexist \"target.iss\"\n; live\n#else\n; dead\n#endif\n"
        assertTrue("An existing literal path is decidable", undecidableMarkers(text).isEmpty())
        assertEquals("The #else of the taken branch must be dimmed", 1, dimmed(text).size)
    }

    fun testIfExistWithMissingLiteralPathIsDecided() {
        val text = "#ifexist \"nope.iss\"\n; dead\n#endif\n"
        assertTrue("A missing literal path is decidable", undecidableMarkers(text).isEmpty())
        assertEquals("The body of the failing #ifexist must be dimmed", 1, dimmed(text).size)
    }

    fun testIfNexistWithMissingLiteralPathIsDecided() {
        val text = "#ifnexist \"nope.iss\"\n; live\n#else\n; dead\n#endif\n"
        assertTrue("A missing literal path is decidable", undecidableMarkers(text).isEmpty())
        assertEquals("The #else of the taken branch must be dimmed", 1, dimmed(text).size)
    }

    fun testIfExistWithDefinedFileNameIsDecided() {
        myFixture.addFileToProject("target.iss", "; content\n")
        val text = "#define file \"target.iss\"\n#ifexist file\n; live\n#else\n; dead\n#endif\n"
        assertTrue("A file name from a #define is decidable", undecidableMarkers(text).isEmpty())
        assertEquals("The #else of the taken branch must be dimmed", 1, dimmed(text).size)
    }

    fun testIfExistWithDefinedNonExistingFileNameIsDecided() {
        val text = "#define demo \"test\"\n#ifexist demo\n; dead\n#else\n; live\n#endif\n"
        assertTrue("A file name from a #define is decidable", undecidableMarkers(text).isEmpty())
        assertEquals("The failing branch must be dimmed", 1, dimmed(text).size)
    }

    fun testDecidableConditionIsNotMarked() {
        assertTrue("A decided condition needs no marker", undecidableMarkers("#if 1\n#endif\n").isEmpty())
    }

    fun testImpureCallIsMarkedWithItsReason() {
        val markers = undecidableMarkers("#if GetEnv(\"X\") == \"\"\n#endif\n")
        assertEquals(1, markers.size)
        assertTrue(
            "The marker should name the offending function, was '${markers.single().description}'",
            markers.single().description!!.contains("GetEnv"),
        )
    }

    // ── diagnostics are suppressed in dead code ────────────────────────────────

    fun testErrorsInsideDeadBranchAreSuppressed() {
        val errors = highlights("#if 0\n#nosuchdirective\n#endif\n").filter { it.severity.name == "ERROR" }
        assertTrue(
            "Dead code must not report errors, got: ${errors.map { it.description }}",
            errors.isEmpty(),
        )
    }

    fun testErrorsInsideLiveBranchAreStillReported() {
        val errors = highlights("#if 1\n#nosuchdirective\n#endif\n").filter { it.severity.name == "ERROR" }
        assertTrue("A compiled branch must still be validated", errors.isNotEmpty())
    }

    fun testErrorsInsideUndecidableBranchAreStillReported() {
        val errors = highlights("#if PREPROCVER > 1\n#nosuchdirective\n#endif\n")
            .filter { it.severity.name == "ERROR" }
        assertTrue(
            "An undecidable branch might be compiled, so it must still be validated",
            errors.isNotEmpty(),
        )
    }

    /**
     * A broken `#if`/`#endif` nesting breaks the file no matter which branch the compiler takes, so the
     * structural check is the one diagnostic that must survive inside dead code.
     */
    fun testStructuralErrorInsideDeadBranchIsStillReported() {
        val errors = highlights("#if 0\n#if 1\n#endif\n")
            .filter { it.severity.name == "ERROR" && it.description?.contains("missing #endif") == true }
        assertTrue("An unterminated #if must be flagged even inside a dead branch", errors.isNotEmpty())
    }

    /** Suppression must be limited to the dead body — the surrounding script stays fully validated. */
    fun testErrorsOutsideDeadBranchAreStillReported() {
        val errors = highlights("#if 0\n; dead\n#endif\n#nosuchdirective\n")
            .filter { it.severity.name == "ERROR" }
        assertTrue("Code after a dead branch must still be validated", errors.isNotEmpty())
    }
}

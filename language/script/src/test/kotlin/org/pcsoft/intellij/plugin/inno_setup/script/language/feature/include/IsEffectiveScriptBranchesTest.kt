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

import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Conditional compilation as applied to the *effective script* — the read-only view behind
 * “Show Effective Script”. A decided `#if` collapses to the body of its live branch; an undecidable one is
 * emitted whole with a marker comment, so the reader can see why it was not resolved.
 */
class IsEffectiveScriptBranchesTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun viewOf(content: String): String {
        val file = myFixture.addFileToProject("main.iss", content + setupTail) as IsScriptFile
        return file.effectiveScriptView().text
    }

    // ── decided blocks collapse ────────────────────────────────────────────────

    fun testActiveBranchSurvivesWithoutItsDirectives() {
        val text = viewOf("#if 1\nAppComments=live\n#endif\n")
        assertTrue("The live branch body must survive: $text", text.contains("AppComments=live"))
        assertFalse("A decided #if line must be dropped: $text", text.contains("#if"))
        assertFalse("A decided #endif line must be dropped: $text", text.contains("#endif"))
    }

    fun testInactiveBranchIsRemoved() {
        val text = viewOf("#if 0\nAppComments=dead\n#endif\n")
        assertFalse("The dead branch body must be gone: $text", text.contains("dead"))
    }

    fun testElseBranchIsPicked() {
        val text = viewOf("#if 0\nAppComments=dead\n#else\nAppComments=live\n#endif\n")
        assertTrue("The #else body must survive: $text", text.contains("AppComments=live"))
        assertFalse("The dead body must be gone: $text", text.contains("dead"))
        assertFalse("The directives must be gone: $text", text.contains("#else"))
    }

    fun testElifChainKeepsOnlyTheTakenBranch() {
        val text = viewOf("#if 0\nA=1\n#elif 1\nB=2\n#elif 1\nC=3\n#else\nD=4\n#endif\n")
        assertTrue("The first true branch must survive: $text", text.contains("B=2"))
        listOf("A=1", "C=3", "D=4").forEach {
            assertFalse("Only the taken branch may survive, found '$it': $text", text.contains(it))
        }
    }

    fun testDefineDrivenBranchIsResolved() {
        val text = viewOf("#define WithDocs 0\n#if WithDocs\nAppComments=docs\n#endif\n")
        assertFalse("A branch disabled by a #define must be gone: $text", text.contains("docs"))
    }

    fun testNestedBlocksCollapseTogether() {
        val text = viewOf("#if 1\n#if 0\nA=dead\n#endif\nB=live\n#endif\n")
        assertTrue("The live inner text must survive: $text", text.contains("B=live"))
        assertFalse("The dead nested body must be gone: $text", text.contains("dead"))
    }

    // ── undecidable blocks are kept whole ──────────────────────────────────────

    fun testUndecidableBlockIsKeptWithASingleMarkerComment() {
        val text = viewOf("#if PREPROCVER > 1\nA=1\n#else\nB=2\n#endif\n")

        assertTrue("Both branches must be kept: $text", text.contains("A=1") && text.contains("B=2"))
        assertTrue("The directives must be kept: $text", text.contains("#if") && text.contains("#endif"))
        assertEquals(
            "Exactly one marker comment must be inserted: $text",
            1,
            text.lines().count { it.trim() == IsEffectiveScriptBranches.UNDECIDED_COMMENT },
        )
    }

    fun testMarkerPrecedesTheBlock() {
        val text = viewOf("#if PREPROCVER > 1\nA=1\n#endif\n")
        val lines = text.lines()
        val marker = lines.indexOfFirst { it.trim() == IsEffectiveScriptBranches.UNDECIDED_COMMENT }
        val opener = lines.indexOfFirst { it.trim().startsWith("#if") }
        assertTrue("A marker must be present: $text", marker >= 0)
        assertTrue("The marker must come before the #if it explains: $text", marker < opener)
    }

    fun testDecidedBlockGetsNoMarker() {
        val text = viewOf("#if 1\nA=1\n#endif\n")
        assertFalse(
            "A decided block must not be marked: $text",
            text.contains(IsEffectiveScriptBranches.UNDECIDED_COMMENT),
        )
    }

    // ── blank lines left behind by pruning ─────────────────────────────────────

    fun testGapLeavesAtMostOneBlankLine() {
        val text = viewOf("A=1\n\n#if 0\n\nB=dead\n\n#endif\n\nC=2\n")
        assertFalse("A pruned block must not leave stacked blank lines: $text", text.contains("\n\n\n"))
        assertTrue("The neighbours must stay separated by one blank line: $text", text.contains("A=1\n\nC=2"))
    }

    fun testBlankLinesAroundAnInnerBranchAreCollapsed() {
        val text = viewOf("#define demo \"test\"\n#ifexist demo\n\nAppComments=dead\n\n#else\n\nAppComments=live\n\n#endif\n")
        assertFalse("Pruning must not stack blank lines: $text", text.contains("\n\n\n"))
        assertTrue(
            "The surviving branch keeps one separating blank line: $text",
            text.contains("#define demo \"test\"\n\nAppComments=live"),
        )
    }

    fun testLeadingBlankLinesOfAPrunedStartAreDropped() {
        val text = viewOf("#if 0\nA=dead\n#endif\n\nB=2\n")
        assertTrue("Nothing may precede the first surviving line: $text", text.startsWith("B=2"))
    }

    // ── interaction with includes ──────────────────────────────────────────────

    /** The point of pruning after inlining: a `#define` from an include decides the includer's `#if`. */
    fun testConditionFromIncludedDefineIsResolved() {
        myFixture.addFileToProject("flags.iss", "#define WithDocs 0\n")
        val text = viewOf("#include \"flags.iss\"\n#if WithDocs\nAppComments=docs\n#endif\n")
        assertFalse("A #define pulled in by #include must decide the branch: $text", text.contains("docs"))
    }

    fun testIncludeRangesStayValidAfterPruning() {
        myFixture.addFileToProject("part.iss", "AppPublisher=Acme\n")
        val file = myFixture.addFileToProject(
            "main.iss",
            "#if 0\nAppComments=dead\n#endif\n#include \"part.iss\"\n$setupTail",
        ) as IsScriptFile

        val view = file.effectiveScriptView()
        assertTrue("Included content must be tinted", view.includeRanges.isNotEmpty())
        val tinted = view.includeRanges.joinToString("") { it.substring(view.text) }
        assertTrue(
            "The tinted range must still cover the included text after pruning, was '$tinted'",
            tinted.contains("AppPublisher=Acme"),
        )
    }
}

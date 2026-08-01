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

package org.pcsoft.intellij.plugin.inno_setup.build.run

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedTestCase

/**
 * Tests for [IsCompilerDefines]. The parse result feeds both the ISCC command line and the editor's
 * conditional-branch analysis, so a discrepancy here would make the editor grey out branches the build
 * actually compiles.
 */
class IsCompilerDefinesTest : IsTimedTestCase() {

    fun testValuelessSymbol() {
        assertEquals(mapOf("DEBUG" to null), IsCompilerDefines.parse("DEBUG"))
    }

    fun testSymbolWithValue() {
        assertEquals(mapOf("VERSION" to "2"), IsCompilerDefines.parse("VERSION=2"))
    }

    fun testLiteralIsccFormIsAccepted() {
        assertEquals(mapOf("DEBUG" to null), IsCompilerDefines.parse("/DDEBUG"))
    }

    fun testSeparatorsCommaSemicolonSpaceAndNewline() {
        val expected = mapOf("A" to null, "B" to "1", "C" to null, "D" to null)
        assertEquals(expected, IsCompilerDefines.parse("A, B=1; C\nD"))
    }

    fun testBlankInputYieldsNothing() {
        assertTrue(IsCompilerDefines.parse("   \n  ").isEmpty())
    }

    fun testSurroundingWhitespaceIsTrimmed() {
        assertEquals(mapOf("A" to "1"), IsCompilerDefines.parse("  A =  1  "))
    }

    /** ISCC itself lets a later `/D` win, so the parser must not keep the earlier one. */
    fun testLaterDefinitionWins() {
        assertEquals(mapOf("A" to "2"), IsCompilerDefines.parse("A=1, A=2"))
    }

    fun testEmptyValueIsPreservedAsEmptyNotAsUndefined() {
        assertEquals(mapOf("A" to ""), IsCompilerDefines.parse("A="))
    }

    fun testIsccArgumentsRoundTrip() {
        assertEquals(
            listOf("/DDEBUG", "/DVERSION=2"),
            IsCompilerDefines.toIsccArguments("DEBUG, VERSION=2"),
        )
    }

    fun testIsccArgumentsOfBlankInputAreEmpty() {
        assertTrue(IsCompilerDefines.toIsccArguments("").isEmpty())
    }
}

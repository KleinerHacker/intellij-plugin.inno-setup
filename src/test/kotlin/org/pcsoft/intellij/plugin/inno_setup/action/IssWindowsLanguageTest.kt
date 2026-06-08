/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.action

import org.junit.Assert.*
import org.junit.Test

/** Pure-logic tests for the Windows-LCID reference data backing LanguageID completion/validation/inlays. */
class IssWindowsLanguageTest {

    @Test
    fun `parseId handles hex, decimal and zero`() {
        assertEquals(0x0409, IssWindowsLanguage.parseId("\$0409"))
        assertEquals(0, IssWindowsLanguage.parseId("0"))
        assertEquals(0, IssWindowsLanguage.parseId("\$0"))
        assertEquals(1033, IssWindowsLanguage.parseId("1033")) // decimal == 0x0409
    }

    @Test
    fun `parseId returns null for malformed values`() {
        assertNull(IssWindowsLanguage.parseId("\$GG"))
        assertNull(IssWindowsLanguage.parseId("abc"))
        assertNull(IssWindowsLanguage.parseId(""))
    }

    @Test
    fun `fromId resolves a curated locale`() {
        assertEquals("German (Germany)", IssWindowsLanguage.fromId(0x0407)?.displayName)
        assertEquals("English (United States)", IssWindowsLanguage.fromId(0x0409)?.displayName)
    }

    @Test
    fun `fromId returns null for a valid but uncurated locale (inlay is omitted)`() {
        // Afrikaans ($0436) is a recognised LCID but not in the curated completion set.
        assertTrue("Afrikaans must be a recognised LCID", 0x0436 in IssWindowsLanguage.validIds)
        assertNull("No curated entry → no inlay mapping", IssWindowsLanguage.fromId(0x0436))
    }

    @Test
    fun `fromId returns null for an unassigned LCID`() {
        assertFalse("\$9999 must not be a recognised LCID", 0x9999 in IssWindowsLanguage.validIds)
        assertNull(IssWindowsLanguage.fromId(0x9999))
    }

    @Test
    fun `every curated entry has a parseable id contained in the valid LCID set`() {
        IssWindowsLanguage.entries.forEach { entry ->
            val numeric = IssWindowsLanguage.parseId(entry.id)
            assertNotNull("Curated id ${entry.id} must parse", numeric)
            assertTrue("Curated id ${entry.id} must be a valid LCID", numeric!! in IssWindowsLanguage.validIds)
        }
    }

    @Test
    fun `fromIssName resolves built-in languages and rejects unknown`() {
        assertEquals(IssScriptLanguage.GERMAN, IssScriptLanguage.fromIssName("german"))
        assertEquals(IssScriptLanguage.GERMAN, IssScriptLanguage.fromIssName("GERMAN"))
        assertNull(IssScriptLanguage.fromIssName("klingon"))
    }
}

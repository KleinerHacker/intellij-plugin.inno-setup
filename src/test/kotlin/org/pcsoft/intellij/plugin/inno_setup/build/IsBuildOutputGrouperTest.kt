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

package org.pcsoft.intellij.plugin.inno_setup.build

import org.junit.Assert.*
import org.junit.Test

class IsBuildOutputGrouperTest {

    private fun group(lines: List<String>): List<IsBuildOutputGrouper.OutputSection> {
        val out = mutableListOf<IsBuildOutputGrouper.OutputSection>()
        val grouper = IsBuildOutputGrouper { out.add(it) }
        lines.forEach { grouper.accept(it) }
        grouper.flush()
        return out
    }

    @Test
    fun `consecutive same-section lines collapse into one section with count`() {
        val sections = group((10..17).map { "Parsing [Setup] section, line $it" })
        assertEquals(1, sections.size)
        assertEquals("[Setup] (8)", sections[0].title)
        assertEquals(8, sections[0].lines.size)
    }

    @Test
    fun `indented detail lines stay inside their section`() {
        val sections = group(
            listOf(
                "Parsing [Languages] section, line 27",
                "   Reading file: C:\\Program Files (x86)\\Inno Setup 6\\Default.isl",
                "   Messages in script file",
                "Reading default messages from Default.isl",
                "Parsing [Languages] section, line 27",
                "   Reading file: C:\\Program Files (x86)\\Inno Setup 6\\Default.isl"
            )
        )
        assertEquals(1, sections.size)
        assertEquals("[Languages] (6)", sections[0].title)
        assertEquals(6, sections[0].lines.size)
    }

    @Test
    fun `lines before the first section form a catch-all group`() {
        val sections = group(
            listOf(
                "Reading file (WizardImageFile)",
                "Reading file (WizardSmallImageFile)",
                "Parsing [Setup] section, line 10"
            )
        )
        assertEquals(2, sections.size)
        assertEquals("Reading file (WizardImageFile) (2)", sections[0].title)
        assertEquals(2, sections[0].lines.size)
        assertEquals("[Setup] (1)", sections[1].title)
    }

    @Test
    fun `different section labels start a new group`() {
        val sections = group(
            listOf(
                "Parsing [Setup] section, line 10",
                "Parsing [Icons] section, line 32",
                "Parsing [Files] section, line 22"
            )
        )
        assertEquals(3, sections.size)
        assertEquals(listOf("[Setup] (1)", "[Icons] (1)", "[Files] (1)"), sections.map { it.title })
    }

    @Test
    fun `flush on empty buffer emits nothing`() {
        assertTrue(group(emptyList()).isEmpty())
    }

    @Test
    fun `section label is detected only for parsing lines`() {
        assertEquals("[Setup]", IsBuildOutputGrouper.sectionLabel("Parsing [Setup] section, line 10"))
        assertNull(IsBuildOutputGrouper.sectionLabel("Reading file (WizardImageFile)"))
        assertNull(IsBuildOutputGrouper.sectionLabel("   Messages in script file"))
    }
}

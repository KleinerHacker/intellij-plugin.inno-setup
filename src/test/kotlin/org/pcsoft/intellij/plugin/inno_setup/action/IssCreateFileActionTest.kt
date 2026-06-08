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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.types.IslLanguageSpec

/**
 * Unit tests for the pure template-generation logic of [IssCreateFileAction].
 *
 * The action's `actionPerformed` is gated behind a modal dialog and cannot run
 * headless, so these tests exercise `buildTemplate` (exposed `@VisibleForTesting`)
 * and [IslLanguageSpec.toIssEntry] directly.
 */
class IssCreateFileActionTest {

    private val action = IssCreateFileAction()

    private val english = IslLanguageSpec(
        "English (United States)", "\$0409", "united-states", true, "english", "compiler:Default.isl"
    )
    private val german = IslLanguageSpec(
        "German (Germany)", "\$0407", "german", true, "german", "compiler:Languages\\German.isl"
    )

    private fun template(
        appName: String = "My App",
        appVersion: String = "1.0",
        languages: List<IslLanguageSpec> = listOf(english)
    ) = action.buildTemplate(appName, appVersion, languages)

    @Test
    fun `template contains the three sections in order`() {
        val text = template()
        val setupIdx = text.indexOf("[Setup]")
        val langIdx = text.indexOf("[Languages]")
        val filesIdx = text.indexOf("[Files]")
        assertTrue("[Setup] must be present", setupIdx >= 0)
        assertTrue("[Languages] must be present", langIdx >= 0)
        assertTrue("[Files] must be present", filesIdx >= 0)
        assertTrue(
            "Sections must appear in order [Setup] < [Languages] < [Files]",
            setupIdx < langIdx && langIdx < filesIdx
        )
    }

    @Test
    fun `setup section carries the provided app name and version`() {
        val text = template(appName = "Acme Tool", appVersion = "2.5")
        assertTrue(text.contains("AppName=Acme Tool"))
        assertTrue(text.contains("AppVersion=2.5"))
        assertTrue(text.contains("DefaultDirName={autopf}\\Acme Tool"))
        assertTrue(text.contains("DefaultGroupName=Acme Tool"))
    }

    @Test
    fun `single language produces exactly one matching entry`() {
        val text = template(languages = listOf(english))
        val entries = text.lines().filter { it.startsWith("Name:") }
        assertEquals(1, entries.size)
        assertEquals(english.toIssEntry(), entries.single())
    }

    @Test
    fun `multiple languages produce one entry each, in order`() {
        val text = template(languages = listOf(english, german))
        val entries = text.lines().filter { it.startsWith("Name:") }
        assertEquals(
            listOf(english.toIssEntry(), german.toIssEntry()),
            entries
        )
    }

    @Test
    fun `empty language list keeps the header but emits no entries`() {
        val text = template(languages = emptyList())
        assertTrue("[Languages] header must still be present", text.contains("[Languages]"))
        assertTrue("No language entries expected", text.lines().none { it.startsWith("Name:") })
    }

    @Test
    fun `toIssEntry formats name and messages file`() {
        assertEquals(
            "Name: \"german\"; MessagesFile: \"compiler:Languages\\German.isl\"",
            german.toIssEntry()
        )
    }
}

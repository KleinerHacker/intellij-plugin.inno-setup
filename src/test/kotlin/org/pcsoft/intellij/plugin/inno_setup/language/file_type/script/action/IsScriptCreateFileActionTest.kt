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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.action

import org.junit.Assert
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.types.IsLanguageDataSpec

/**
 * Unit tests for the pure template-generation logic of [IsScriptCreateFileAction].
 *
 * The action's `actionPerformed` is gated behind a modal dialog and cannot run
 * headless, so these tests exercise `buildTemplate` (exposed `@VisibleForTesting`)
 * and [org.pcsoft.intellij.plugin.inno_setup.types.IsLanguageDataSpec.toIssEntry] directly.
 */
class IsScriptCreateFileActionTest {

    private val action = IsScriptCreateFileAction()

    private val english = IsLanguageDataSpec(
        "English (United States)", "\$0409", "united-states", true, "english", "compiler:Default.isl"
    )
    private val german = IsLanguageDataSpec(
        "German (Germany)", "\$0407", "german", true, "german", "compiler:Languages\\German.isl"
    )

    private fun template(
        appName: String = "My App",
        appVersion: String = "1.0",
        languages: List<IsLanguageDataSpec> = listOf(english)
    ) = action.buildTemplate(appName, appVersion, languages)

    @Test
    fun `template contains the three sections in order`() {
        val text = template()
        val setupIdx = text.indexOf("[Setup]")
        val langIdx = text.indexOf("[Languages]")
        val filesIdx = text.indexOf("[Files]")
        Assert.assertTrue("[Setup] must be present", setupIdx >= 0)
        Assert.assertTrue("[Languages] must be present", langIdx >= 0)
        Assert.assertTrue("[Files] must be present", filesIdx >= 0)
        Assert.assertTrue(
            "Sections must appear in order [Setup] < [Languages] < [Files]",
            setupIdx < langIdx && langIdx < filesIdx
        )
    }

    @Test
    fun `setup section carries the provided app name and version`() {
        val text = template(appName = "Acme Tool", appVersion = "2.5")
        Assert.assertTrue(text.contains("AppName=Acme Tool"))
        Assert.assertTrue(text.contains("AppVersion=2.5"))
        Assert.assertTrue(text.contains("DefaultDirName={autopf}\\Acme Tool"))
        Assert.assertTrue(text.contains("DefaultGroupName=Acme Tool"))
    }

    @Test
    fun `single language produces exactly one matching entry`() {
        val text = template(languages = listOf(english))
        val entries = text.lines().filter { it.startsWith("Name:") }
        Assert.assertEquals(1, entries.size)
        Assert.assertEquals(english.toIssEntry(), entries.single())
    }

    @Test
    fun `multiple languages produce one entry each, in order`() {
        val text = template(languages = listOf(english, german))
        val entries = text.lines().filter { it.startsWith("Name:") }
        Assert.assertEquals(
            listOf(english.toIssEntry(), german.toIssEntry()),
            entries
        )
    }

    @Test
    fun `empty language list keeps the header but emits no entries`() {
        val text = template(languages = emptyList())
        Assert.assertTrue("[Languages] header must still be present", text.contains("[Languages]"))
        Assert.assertTrue("No language entries expected", text.lines().none { it.startsWith("Name:") })
    }

    @Test
    fun `toIssEntry formats name and messages file`() {
        Assert.assertEquals(
            "Name: \"german\"; MessagesFile: \"compiler:Languages\\German.isl\"",
            german.toIssEntry()
        )
    }
}
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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.action

import org.junit.Assert
import org.junit.Test

/**
 * Unit tests for the pure template-generation logic of [IsLanguageCreateFileAction].
 *
 * The action's `actionPerformed` is gated behind a modal dialog and cannot run headless, so these
 * tests exercise `buildTemplate` (exposed `@VisibleForTesting`) directly.
 */
class IsLanguageCreateFileActionTest {

    private val action = IsLanguageCreateFileAction()

    private fun template(
        languageName: String = "English",
        languageId: String = "\$0409"
    ) = action.buildTemplate(languageName, languageId)

    @Test
    fun `template contains the three sections in order`() {
        val text = template()
        val langOptionsIdx = text.indexOf("[LangOptions]")
        val messagesIdx = text.indexOf("[Messages]")
        val customIdx = text.indexOf("[CustomMessages]")
        Assert.assertTrue("[LangOptions] must be present", langOptionsIdx >= 0)
        Assert.assertTrue("[Messages] must be present", messagesIdx >= 0)
        Assert.assertTrue("[CustomMessages] must be present", customIdx >= 0)
        Assert.assertTrue(
            "Sections must appear in order [LangOptions] < [Messages] < [CustomMessages]",
            langOptionsIdx < messagesIdx && messagesIdx < customIdx
        )
    }

    @Test
    fun `lang options carries the provided language name and id`() {
        val text = template(languageName = "German (Germany)", languageId = "\$0407")
        Assert.assertTrue(text.contains("LanguageName=German (Germany)"))
        Assert.assertTrue(text.contains("LanguageID=\$0407"))
    }

    @Test
    fun `language name and id are emitted exactly once each`() {
        val text = template()
        Assert.assertEquals(1, text.lines().count { it.startsWith("LanguageName=") })
        Assert.assertEquals(1, text.lines().count { it.startsWith("LanguageID=") })
    }

    @Test
    fun `lang options directives sit directly under their header`() {
        val lines = template().lines()
        val header = lines.indexOf("[LangOptions]")
        Assert.assertTrue("[LangOptions] header expected", header >= 0)
        Assert.assertEquals("LanguageName=English", lines[header + 1])
        Assert.assertEquals("LanguageID=\$0409", lines[header + 2])
    }
}

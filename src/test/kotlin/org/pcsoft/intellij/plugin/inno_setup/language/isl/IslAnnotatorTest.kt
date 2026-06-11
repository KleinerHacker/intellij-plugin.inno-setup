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

package org.pcsoft.intellij.plugin.inno_setup.language.isl

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Tests for the ISL section restriction ([org.pcsoft.intellij.plugin.inno_setup.language.isl.parsing.IslAnnotator])
 * plus the .isl-specific relaxations in the shared ISI annotator (no required [Setup]).
 */
class IslAnnotatorTest : BasePlatformTestCase() {

    private fun islHighlights(content: String) =
        myFixture.run { configureByText(IslFileType.INSTANCE, content); doHighlighting() }

    private fun issHighlights(content: String) =
        myFixture.run { configureByText(IssFileType.INSTANCE, content); doHighlighting() }

    private fun notAllowedError(content: String) =
        islHighlights(content).any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("not allowed in Inno Setup language", ignoreCase = true) == true
        }

    private fun missingSectionError(highlights: List<com.intellij.codeInsight.daemon.impl.HighlightInfo>) =
        highlights.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Required section", ignoreCase = true) == true
        }

    fun testLangOptionsIsAllowed() {
        assertFalse(notAllowedError("[LangOptions]\nLanguageName=English\n"))
    }

    fun testMessagesIsAllowed() {
        assertFalse(notAllowedError("[Messages]\nWelcomeLabel1=Hello\n"))
    }

    fun testCustomMessagesIsAllowed() {
        assertFalse(notAllowedError("[CustomMessages]\nMyMsg=Hello\n"))
    }

    fun testSetupSectionIsNotAllowed() {
        assertTrue(notAllowedError("[Setup]\nAppName=Test\n"))
    }

    fun testFilesSectionIsNotAllowed() {
        assertTrue(notAllowedError("[Files]\nSource: \"a.txt\"; DestDir: \"{app}\"\n"))
    }

    private fun errorContaining(
        highlights: List<com.intellij.codeInsight.daemon.impl.HighlightInfo>,
        needle: String
    ) = highlights.any {
        it.severity == HighlightSeverity.ERROR &&
                it.description?.contains(needle, ignoreCase = true) == true
    }

    // ── ISL required sections / directives ────────────────────────────────────

    fun testIslSetupNotRequired() {
        // [Setup] is a script-only requirement — never demanded in .isl.
        assertFalse(errorContaining(islHighlights("[LangOptions]\nLanguageName=x\nLanguageID=\$0409\n"), "[setup]"))
    }

    fun testIslRequiresLangOptions() {
        // .isl must contain [LangOptions]; its absence is a file-level error.
        val h = islHighlights("[Messages]\nWelcomeLabel1=Hi\n")
        assertTrue("Missing [LangOptions] must be reported in .isl", errorContaining(h, "[langoptions]"))
        assertFalse("[Setup] must not be demanded in .isl", errorContaining(h, "[setup]"))
    }

    fun testIslLangOptionsRequiresLanguageNameAndId() {
        val h = islHighlights("[LangOptions]\nLanguageCodePage=0\n")
        assertTrue(
            "LanguageName/LanguageID must be required directives in .isl",
            errorContaining(h, "Required directive")
        )
    }

    fun testIslCompleteLangOptionsHasNoRequiredErrors() {
        val h = islHighlights("[LangOptions]\nLanguageName=English\nLanguageID=\$0409\n")
        assertFalse("Complete [LangOptions] must not report missing section", errorContaining(h, "Required section"))
        assertFalse("Complete [LangOptions] must not report missing directive", errorContaining(h, "Required directive"))
    }

    // ── Contrast: .iss keeps its own (different) requirements ──────────────────

    fun testRequiredSetupStillEnforcedInIssFile() {
        // The same content as a .iss script must still demand [Setup].
        assertTrue(missingSectionError(issHighlights("[Messages]\nWelcomeLabel1=Hi\n")))
    }

    fun testIssDoesNotRequireLangOptions() {
        // A valid .iss script without [LangOptions] is fine.
        val h = issHighlights("[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertFalse("LangOptions is not required in .iss", errorContaining(h, "[langoptions]"))
    }

    fun testIssLangOptionsWithoutIdentityHasNoRequiredDirectiveError() {
        // LanguageName/LanguageID are required only in .isl, not in scripts.
        val h = issHighlights("[Setup]\nAppName=Test\nAppVersion=1.0\n[LangOptions]\nLanguageCodePage=0\n")
        assertFalse("LanguageName/LanguageID not required in .iss", errorContaining(h, "Required directive"))
    }
}

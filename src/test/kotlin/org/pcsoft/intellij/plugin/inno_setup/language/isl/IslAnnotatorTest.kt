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

    fun testNoRequiredSetupInIslFile() {
        // .isl files have no required sections — a missing [Setup] must not be reported.
        assertFalse(missingSectionError(islHighlights("[Messages]\nWelcomeLabel1=Hi\n")))
    }

    fun testRequiredSetupStillEnforcedInIssFile() {
        // Contrast: the same content as a .iss script must still demand [Setup].
        assertTrue(missingSectionError(issHighlights("[Messages]\nWelcomeLabel1=Hi\n")))
    }
}

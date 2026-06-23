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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsService

/**
 * Integration tests for [IsIncludeContentInlayHintsProvider]: drives the provider over real PSI (including a
 * resolved `#include` target) and inspects the editor's block-inlay model after a highlighting pass.
 */
class IsIncludeContentInlayHintsTest : BasePlatformTestCase() {

    override fun tearDown() {
        try {
            IsSettingsService.getInstance().state.showIncludeContentInlayHints = true
        } finally {
            super.tearDown()
        }
    }

    /** Number of block inlays contributed for the configured host file. */
    private fun blockInlayCount(): Int {
        myFixture.doHighlighting()
        val doc = myFixture.editor.document
        return myFixture.editor.inlayModel
            .getBlockElementsInRange(0, doc.textLength)
            .size
    }

    private fun configureHostWithInclude(includePath: String, includedContent: String?) {
        if (includedContent != null) {
            myFixture.addFileToProject("common.iss", includedContent)
        }
        val host = myFixture.addFileToProject(
            "main.iss",
            "[Setup]\nAppName=Test\n#include \"$includePath\"\n"
        )
        myFixture.configureFromExistingVirtualFile(host.virtualFile)
    }

    fun testIncludeShowsContentInlays() {
        // The per-line block elements are grouped by the sink into one stacked block inlay above the
        // directive line — so the included content is shown with its line breaks preserved.
        configureHostWithInclude("common.iss", "[Files]\nSource: a.txt\n")
        assertEquals("A block inlay is shown for the included content", 1, blockInlayCount())
    }

    fun testMissingIncludeFileShowsNoInlay() {
        configureHostWithInclude("does-not-exist.iss", includedContent = null)
        assertEquals("No inlay when the include target is missing", 0, blockInlayCount())
    }

    fun testDisabledSettingShowsNoInlay() {
        IsSettingsService.getInstance().state.showIncludeContentInlayHints = false
        configureHostWithInclude("common.iss", "[Files]\nSource: a.txt\n")
        assertEquals("No inlay when the feature is disabled", 0, blockInlayCount())
    }
}

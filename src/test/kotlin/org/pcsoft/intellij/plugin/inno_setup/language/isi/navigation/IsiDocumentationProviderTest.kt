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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IsiDocumentationProviderTest : BasePlatformTestCase() {

    private val provider = IsiDocumentationProvider()

    private fun docFor(content: String): String? {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        val ctx = myFixture.file.findElementAt(myFixture.caretOffset)
        val target = provider.getCustomDocumentationElement(
            myFixture.editor, myFixture.file, ctx, myFixture.caretOffset
        ) ?: ctx ?: return null
        return provider.generateDoc(target, ctx)
    }

    fun testSectionNameDoc() {
        val doc = docFor("[Set<caret>up]\nAppName=My App\n")
        assertNotNull("Expected doc for section name", doc)
        assertTrue(doc!!.contains("Setup"))
    }

    fun testDirectiveKeyDoc() {
        val doc = docFor("[Setup]\nAppNa<caret>me=My App\n")
        assertNotNull("Expected doc for directive key", doc)
        assertTrue(doc!!.contains("AppName"))
    }

    fun testFlagDoc() {
        val doc = docFor("[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: ignoreversi<caret>on\n")
        assertNotNull("Expected doc for the ignoreversion flag", doc)
        assertTrue(doc!!.contains("ignoreversion"))
        assertTrue("Flag doc must declare it is a flag", doc.contains("flag"))
    }

    fun testConstantDoc() {
        val doc = docFor("[Files]\nSource: \"app.exe\"; DestDir: \"{a<caret>pp}\"\n")
        assertNotNull("Expected doc for the {app} constant", doc)
        assertTrue(doc!!.contains("app"))
    }

    fun testValueContextReturnsNull() {
        val doc = docFor("[Setup]\nAppName=MyA<caret>pp\n")
        assertNull("Value text should not produce docs", doc)
    }

    // ── Version section in doc ────────────────────────────────────────────────

    fun testDirectiveWithSinceShowsVersionSection() {
        // ArchiveExtraction has since="6.5"
        val doc = docFor("[Setup]\nAppName=Test\nAppVersion=1.0\nArchiveExtr<caret>action=full\n")
        assertNotNull("Expected doc for ArchiveExtraction", doc)
        assertTrue(
            "Doc for ArchiveExtraction (since 6.5) must contain 'since' version info",
            doc!!.contains("6.5")
        )
        assertTrue(
            "Doc must mention 'Available since'",
            doc.contains("Available since", ignoreCase = true)
        )
    }

    fun testDirectiveWithoutVersionShowsNoVersionSection() {
        // AppName has no since/until
        val doc = docFor("[Setup]\nAppNa<caret>me=Test\n")
        assertNotNull("Expected doc for AppName", doc)
        assertFalse(
            "Doc for AppName (no version) must not contain version section",
            doc!!.contains("Available since", ignoreCase = true)
        )
        assertFalse(
            "Doc for AppName must not contain 'Removed in'",
            doc.contains("Removed in", ignoreCase = true)
        )
    }

    fun testConstantRemovedInShowsUntilVersion() {
        // {hwnd} has until="6.4"
        val doc = docFor("[Files]\nSource: \"app.exe\"; DestDir: \"{hw<caret>nd}\"\n")
        assertNotNull("Expected doc for {hwnd}", doc)
        assertTrue(
            "Doc for {hwnd} (until 6.4) must contain 'Removed in'",
            doc!!.contains("Removed in", ignoreCase = true)
        )
        assertTrue(
            "Doc for {hwnd} must mention version 6.4",
            doc.contains("6.4")
        )
    }

    fun testFlagWithSinceShowsVersionSection() {
        // signcheck flag has since="6.4"
        val doc = docFor("[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: signche<caret>ck\n")
        assertNotNull("Expected doc for signcheck flag", doc)
        assertTrue(
            "Doc for signcheck flag (since 6.4) must contain '6.4'",
            doc!!.contains("6.4")
        )
    }
}

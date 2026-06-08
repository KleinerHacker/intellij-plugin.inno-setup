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

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveEntryEx

/**
 * Tests for {cm:MessageName} ↔ [CustomMessages] declarations: resolve, unresolved highlighting,
 * find usages and rename (including keeping language variants in sync).
 */
class IsiCustomMessageReferenceTest : BasePlatformTestCase() {

    private fun cmBody(file: IssFile): IsiConstantBody =
        PsiTreeUtil.findChildrenOfType(file, IsiConstantBody::class.java)
            .first { it.text.startsWith("cm:", ignoreCase = true) }

    private fun declaration(file: IssFile, name: String): IsiDirectiveEntry =
        file.findSections("CustomMessages")
            .flatMap { it.directiveEntryList }
            .first { (it as IsiDirectiveEntryEx).customMessageName().equals(name, ignoreCase = true) }

    // ── resolve ──────────────────────────────────────────────────────────────

    fun testCmResolvesToDeclaration() {
        val file = myFixture.configureByText(
            IssFileType.INSTANCE,
            "[CustomMessages]\nGreeting=Hello\n[Setup]\nAppComments={cm:Greeting}\n"
        ) as IssFile
        val ref = cmBody(file).references.first()
        val resolved = ref.resolve()
        assertNotNull("Expected {cm:Greeting} to resolve", resolved)
        assertEquals("Greeting", (resolved as IsiDirectiveEntryEx).customMessageName())
    }

    fun testCmResolvesToLanguagePrefixedDeclaration() {
        val file = myFixture.configureByText(
            IssFileType.INSTANCE,
            "[CustomMessages]\nenglish.Greeting=Hello\n[Setup]\nAppComments={cm:Greeting}\n"
        ) as IssFile
        val ref = cmBody(file).references.first()
        assertNotNull("{cm:Greeting} must resolve to english.Greeting", ref.resolve())
    }

    fun testUnknownCmDoesNotResolve() {
        val file = myFixture.configureByText(
            IssFileType.INSTANCE,
            "[Setup]\nAppComments={cm:Missing}\n"
        ) as IssFile
        assertNull("Unknown custom message must not resolve", cmBody(file).references.first().resolve())
    }

    // ── red highlighting ───────────────────────────────────────────────────────

    fun testUnresolvedCmIsHighlightedAsError() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Setup]\nAppComments={cm:Missing}\n")
        val start = myFixture.editor.document.text.indexOf("Missing")
        val hasError = myFixture.doHighlighting().any {
            it.severity == HighlightSeverity.ERROR &&
                    it.startOffset <= start && it.endOffset >= start + "Missing".length
        }
        assertTrue("Unresolved {cm:Missing} must be highlighted in red (error)", hasError)
    }

    fun testResolvedCmHasNoError() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "[CustomMessages]\nGreeting=Hello\n[Setup]\nAppComments={cm:Greeting}\n"
        )
        val start = myFixture.editor.document.text.lastIndexOf("Greeting")
        val hasError = myFixture.doHighlighting().any {
            it.severity == HighlightSeverity.ERROR &&
                    it.startOffset <= start && it.endOffset >= start + "Greeting".length
        }
        assertFalse("Resolved {cm:Greeting} must not be flagged", hasError)
    }

    // ── find usages ────────────────────────────────────────────────────────────

    fun testFindUsagesListsAllCmUsages() {
        val file = myFixture.configureByText(
            IssFileType.INSTANCE,
            "[CustomMessages]\nGreeting=Hello\n[Setup]\nA={cm:Greeting}\nB={cm:Greeting}\n"
        ) as IssFile
        val refs = ReferencesSearch.search(declaration(file, "Greeting")).findAll()
        assertEquals("Expected both {cm:Greeting} usages", 2, refs.size)
    }

    // ── rename ─────────────────────────────────────────────────────────────────

    fun testRenameDeclarationUpdatesCmUsage() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "[CustomMessages]\nGree<caret>ting=Hello\n[Setup]\nAppComments={cm:Greeting}\n"
        )
        myFixture.renameElementAtCaret("Hello")
        myFixture.checkResult(
            "[CustomMessages]\nHello=Hello\n[Setup]\nAppComments={cm:Hello}\n"
        )
    }

    fun testRenameFromUsageUpdatesDeclaration() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "[CustomMessages]\nGreeting=Hello\n[Setup]\nAppComments={cm:Gree<caret>ting}\n"
        )
        myFixture.renameElementAtCaret("Hello")
        myFixture.checkResult(
            "[CustomMessages]\nHello=Hello\n[Setup]\nAppComments={cm:Hello}\n"
        )
    }

    fun testRenameKeepsLanguageVariantsInSync() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "[CustomMessages]\nenglish.Gree<caret>ting=Hello\ngerman.Greeting=Hallo\n" +
                "[Setup]\nAppComments={cm:Greeting}\n"
        )
        myFixture.renameElementAtCaret("Hello")
        myFixture.checkResult(
            "[CustomMessages]\nenglish.Hello=Hello\ngerman.Hello=Hallo\n" +
                "[Setup]\nAppComments={cm:Hello}\n"
        )
    }
}

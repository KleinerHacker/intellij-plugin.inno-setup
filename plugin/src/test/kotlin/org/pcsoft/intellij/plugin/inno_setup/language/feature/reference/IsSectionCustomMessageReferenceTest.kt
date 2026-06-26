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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntryEx
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for {cm:MessageName} ↔ `\[CustomMessages]` declarations: resolve, unresolved highlighting,
 * find usages and rename (including keeping language variants in sync).
 */
class IsSectionCustomMessageReferenceTest : IsTimedBasePlatformTestCase() {

    private fun cmBody(file: IsScriptFile): IsSectionConstantBody =
        PsiTreeUtil.findChildrenOfType(file, IsSectionConstantBody::class.java)
            .first { it.text.startsWith("cm:", ignoreCase = true) }

    private fun declaration(file: IsScriptFile, name: String): IsSectionDirectiveEntry =
        file.findSections("CustomMessages")
            .flatMap { it.directiveEntryList }
            .first { (it as IsSectionDirectiveEntryEx).customMessageName().equals(name, ignoreCase = true) }

    // ── resolve ──────────────────────────────────────────────────────────────

    fun testCmResolvesToDeclaration() {
        val file = myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[CustomMessages]\nGreeting=Hello\n[Setup]\nAppComments={cm:Greeting}\n"
        ) as IsScriptFile
        val ref = cmBody(file).references.first()
        val resolved = ref.resolve()
        assertNotNull("Expected {cm:Greeting} to resolve", resolved)
        assertEquals("Greeting", (resolved as IsSectionDirectiveEntryEx).customMessageName())
    }

    fun testCmResolvesToLanguagePrefixedDeclaration() {
        val file = myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[CustomMessages]\nenglish.Greeting=Hello\n[Setup]\nAppComments={cm:Greeting}\n"
        ) as IsScriptFile
        val ref = cmBody(file).references.first()
        assertNotNull("{cm:Greeting} must resolve to english.Greeting", ref.resolve())
    }

    fun testUnknownCmDoesNotResolve() {
        val file = myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Setup]\nAppComments={cm:Missing}\n"
        ) as IsScriptFile
        assertNull("Unknown custom message must not resolve", cmBody(file).references.first().resolve())
    }

    // ── red highlighting ───────────────────────────────────────────────────────

    fun testUnresolvedCmIsHighlightedAsError() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nAppComments={cm:Missing}\n")
        val start = myFixture.editor.document.text.indexOf("Missing")
        val hasError = myFixture.doHighlighting().any {
            it.severity == HighlightSeverity.ERROR &&
                    it.startOffset <= start && it.endOffset >= start + "Missing".length
        }
        assertTrue("Unresolved {cm:Missing} must be highlighted in red (error)", hasError)
    }

    fun testResolvedCmHasNoError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
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
            IsScriptFileType.INSTANCE,
            "[CustomMessages]\nGreeting=Hello\n[Setup]\nA={cm:Greeting}\nB={cm:Greeting}\n"
        ) as IsScriptFile
        val refs = ReferencesSearch.search(declaration(file, "Greeting")).findAll()
        assertEquals("Expected both {cm:Greeting} usages", 2, refs.size)
    }

    // ── rename ─────────────────────────────────────────────────────────────────

    fun testRenameDeclarationUpdatesCmUsage() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[CustomMessages]\nGree<caret>ting=Hello\n[Setup]\nAppComments={cm:Greeting}\n"
        )
        myFixture.renameElementAtCaret("Hello")
        myFixture.checkResult(
            "[CustomMessages]\nHello=Hello\n[Setup]\nAppComments={cm:Hello}\n"
        )
    }

    fun testRenameFromUsageUpdatesDeclaration() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[CustomMessages]\nGreeting=Hello\n[Setup]\nAppComments={cm:Gree<caret>ting}\n"
        )
        myFixture.renameElementAtCaret("Hello")
        myFixture.checkResult(
            "[CustomMessages]\nHello=Hello\n[Setup]\nAppComments={cm:Hello}\n"
        )
    }

    fun testRenameKeepsLanguageVariantsInSync() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
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

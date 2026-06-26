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
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntry

/**
 * Tests for the `lang.` key prefix (in `\[Messages]`/`\[CustomMessages]`) ↔ `\[Languages] Name`:
 * resolve, unknown-prefix highlighting, find usages and rename.
 */
class IsSectionLanguagePrefixReferenceTest : IsTimedBasePlatformTestCase() {

    private val LANGUAGES = "[Languages]\nName: english; MessagesFile: \"compiler:Default.isl\"\n"

    private fun prefixedEntry(file: IsScriptFile): IsSectionDirectiveEntry =
        PsiTreeUtil.findChildrenOfType(file, IsSectionDirectiveEntry::class.java)
            .first { it.keyText().contains('.') }

    fun testPrefixResolvesToLanguagesName() {
        val file = myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            LANGUAGES + "[Messages]\nenglish.WelcomeLabel1=Welcome\n"
        ) as IsScriptFile
        val resolved = prefixedEntry(file).references.first().resolve()
        assertNotNull("english. prefix must resolve to a [Languages] Name", resolved)
        assertTrue(resolved!!.text.contains("english", ignoreCase = true))
    }

    fun testReferenceFoundAtCaretOnPrefix() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            LANGUAGES + "[Messages]\nengl<caret>ish.WelcomeLabel1=Welcome\n"
        )
        val ref = myFixture.getReferenceAtCaretPosition()
        assertNotNull("findReferenceAt must return the prefix reference", ref)
        assertNotNull("The prefix reference must resolve to [Languages] Name", ref!!.resolve())
    }

    fun testReferenceFoundAtCaretQuotedName() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n" +
                    "[Messages]\nengl<caret>ish.WelcomeLabel1=Welcome\n"
        )
        val ref = myFixture.getReferenceAtCaretPosition()
        assertNotNull("findReferenceAt must return the prefix reference (quoted Name)", ref)
        assertNotNull("Prefix must resolve to the quoted [Languages] Name", ref!!.resolve())
    }

    fun testReferenceFoundAtCaretInCustomMessages() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n" +
                    "[CustomMessages]\nengl<caret>ish.MyMsg=Hello\n"
        )
        val ref = myFixture.getReferenceAtCaretPosition()
        assertNotNull("findReferenceAt must return the prefix reference in [CustomMessages]", ref)
        assertNotNull("Prefix must resolve in [CustomMessages]", ref!!.resolve())
    }

    fun testGotoDeclarationNavigatesToLanguagesName() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            LANGUAGES + "[Messages]\nengl<caret>ish.WelcomeLabel1=Welcome\n"
        )
        val util = com.intellij.codeInsight.TargetElementUtil.getInstance()
        val target = util.findTargetElement(myFixture.editor, util.allAccepted, myFixture.caretOffset)
        assertNotNull("Go to Declaration (Ctrl+B) must reach the [Languages] Name", target)
        assertTrue("Target must be the english Name", target!!.text.contains("english", ignoreCase = true))
    }

    fun testGotoDeclarationQuotedLanguageName() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n" +
                    "[Messages]\nengl<caret>ish.WelcomeLabel1=Welcome\n"
        )
        val util = com.intellij.codeInsight.TargetElementUtil.getInstance()
        val target = util.findTargetElement(myFixture.editor, util.allAccepted, myFixture.caretOffset)
        assertNotNull("Ctrl+B must reach a quoted [Languages] Name too", target)
        assertTrue(target!!.text.contains("english", ignoreCase = true))
    }

    fun testGotoDeclarationFromCustomMessagesPrefix() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            LANGUAGES + "[CustomMessages]\nengl<caret>ish.MyMsg=Hello\n"
        )
        val util = com.intellij.codeInsight.TargetElementUtil.getInstance()
        val target = util.findTargetElement(myFixture.editor, util.allAccepted, myFixture.caretOffset)
        assertNotNull("Ctrl+B on a [CustomMessages] prefix must reach the [Languages] Name", target)
        assertTrue("Target must be the english Name", target!!.text.contains("english", ignoreCase = true))
    }

    fun testUnknownPrefixDoesNotResolve() {
        val file = myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            LANGUAGES + "[Messages]\nde.WelcomeLabel1=Willkommen\n"
        ) as IsScriptFile
        assertNull("An undeclared prefix must not resolve", prefixedEntry(file).references.first().resolve())
    }

    fun testUnknownPrefixIsHighlightedAsError() {
        val text = LANGUAGES + "[Messages]\nde.WelcomeLabel1=Willkommen\n"
        myFixture.configureByText(IsScriptFileType.INSTANCE, text)
        val start = text.indexOf("de.")
        val hasError = myFixture.doHighlighting().any {
            it.severity == HighlightSeverity.ERROR && it.startOffset <= start && it.endOffset >= start + 2
        }
        assertTrue("An undeclared lang. prefix must be highlighted in red", hasError)
    }

    fun testFindUsagesListsPrefixUsages() {
        val file = myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            LANGUAGES + "[Messages]\nenglish.WelcomeLabel1=Welcome\nenglish.ButtonNext=Next\n"
        ) as IsScriptFile
        val namePair = file.findSections("Languages").flatMap { it.nameDeclarations }.first()
        val refs = ReferencesSearch.search(namePair).findAll()
        assertEquals("Both english. prefixes must be found", 2, refs.size)
    }

    fun testRenameLanguageNameUpdatesPrefixes() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: eng<caret>lish; MessagesFile: \"compiler:Default.isl\"\n" +
                    "[Messages]\nenglish.WelcomeLabel1=Welcome\n"
        )
        myFixture.renameElementAtCaret("british")
        myFixture.checkResult(
            "[Languages]\nName: british; MessagesFile: \"compiler:Default.isl\"\n" +
                    "[Messages]\nbritish.WelcomeLabel1=Welcome\n"
        )
    }

    fun testRenameQuotedLanguageNameUpdatesPrefixes() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"eng<caret>lish\"; MessagesFile: \"compiler:Default.isl\"\n" +
                    "[Messages]\nenglish.WelcomeLabel1=Welcome\n"
        )
        myFixture.renameElementAtCaret("british")
        myFixture.checkResult(
            "[Languages]\nName: \"british\"; MessagesFile: \"compiler:Default.isl\"\n" +
                    "[Messages]\nbritish.WelcomeLabel1=Welcome\n"
        )
    }
}

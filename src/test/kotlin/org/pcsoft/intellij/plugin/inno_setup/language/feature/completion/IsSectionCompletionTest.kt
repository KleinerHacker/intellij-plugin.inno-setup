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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion

import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.JBColor
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService

class IsSectionCompletionTest : BasePlatformTestCase() {

    /** Foreground color the completion popup would render for the given key, or null if absent. */
    private fun foregroundOf(name: String): java.awt.Color? =
        myFixture.lookupElements?.firstOrNull { element ->
            LookupElementPresentation().also { element.renderElement(it) }.itemText == name
        }?.let { element ->
            LookupElementPresentation().also { element.renderElement(it) }.itemTextForeground
        }

    // ── Section name completion ───────────────────────────────────────────────

    fun testSectionNameInsertHandlerNoDoubledBracketWhenBracketAlreadyPresent() {
        // The IDE often auto-pairs '[' with ']'. In that case the insert handler must
        // skip past the existing ']' instead of inserting a second one.
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[<caret>]")
        myFixture.completeBasic()
        assertNotNull("Expected completion popup with section names", myFixture.lookup)
        myFixture.lookup!!.currentItem = myFixture.lookupElements!!.first { it.lookupString == "Setup" }
        myFixture.type("\n")
        val text = myFixture.editor.document.text
        assertFalse("Insert handler must not produce a doubled ']'", "]]\n" in text || "]\n]" in text)
        assertTrue("Result must start with '[Setup]\\n'", text.startsWith("[Setup]\n"))
    }

    fun testSectionNameInsertHandlerAddsBracketWhenAbsent() {
        // When there is no ']' yet, the insert handler must add it.
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[<caret>")
        myFixture.completeBasic()
        assertNotNull("Expected completion popup with section names", myFixture.lookup)
        myFixture.lookup!!.currentItem = myFixture.lookupElements!!.first { it.lookupString == "Setup" }
        myFixture.type("\n")
        val text = myFixture.editor.document.text
        assertTrue("Insert handler must add ']\\n' when not already present", text.startsWith("[Setup]\n"))
    }

    fun testSectionNameCompletion() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[<caret>")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected section name suggestions", variants)
        assertTrue("Expected 'Setup' in section names", "Setup" in variants!!)
        assertTrue("Expected 'Files' in section names", "Files" in variants)
    }

    // ── Directive key completion ([Setup]-style Key=Value) ────────────────────

    fun testDirectiveKeyCompletionAtLineStart() {
        // Caret at the very beginning of an empty line inside [Setup]
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\n<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected directive key suggestions on empty line in [Setup]", variants)
        assertTrue("Expected 'AppName' in directive key suggestions", "AppName" in variants!!)
        assertTrue("Expected 'AppVersion' in directive key suggestions", "AppVersion" in variants)
    }

    fun testDirectiveKeyCompletionMidWord() {
        // Caret in the middle of a partially-typed key that already has '=' on the line
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nApp<caret>=My Program\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected directive key suggestions mid-word", variants)
        assertTrue("Expected 'AppName' in suggestions", "AppName" in variants!!)
    }

    // ── Parameter key completion ([Files]-style Key: Value) ───────────────────

    fun testParamKeyCompletionAtLineStart() {
        // Caret at the beginning of an empty line inside [Files]
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Files]\n<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected parameter key suggestions on empty line in [Files]", variants)
        assertTrue("Expected 'Source' in parameter key suggestions", "Source" in variants!!)
        assertTrue("Expected 'DestDir' in parameter key suggestions", "DestDir" in variants)
    }

    fun testParamKeyCompletionMidWord() {
        // Caret in the middle of a partially-typed key that already has ': ...' on the line
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Files]\nSo<caret>: \"app.exe\"\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected parameter key suggestions mid-word", variants)
        assertTrue("Expected 'Source' in suggestions", "Source" in variants!!)
    }

    fun testParamKeyCompletionAfterSemicolon() {
        // Caret after a "; " separator on a [Files] line — must still offer keys
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Files]\nSource: \"app.exe\"; <caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected parameter key suggestions after ';'", variants)
        assertTrue("Expected 'DestDir' in suggestions after ';'", "DestDir" in variants!!)
    }

    fun testParamKeyDuplicateIsPerLineNotPerSection() {
        // DestDir is used on the FIRST line; on the second line it must NOT be
        // flagged as a duplicate (red), because parameter keys are per-line.
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"\nSource: \"b.exe\"; <caret>\n"
        )
        myFixture.completeBasic()
        assertEquals(
            "DestDir used only on another line must not be marked as duplicate",
            JBColor.foreground(), foregroundOf("DestDir")
        )
        assertEquals(
            "Source used on the current line must be marked as duplicate (red)",
            JBColor.RED, foregroundOf("Source")
        )
    }

    // ── Boolean value completion ──────────────────────────────────────────────────

    fun testBooleanValueCompletionForDirective() {
        // CloseApplications is a boolean directive → must offer yes/no
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nCloseApplications=<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected boolean value suggestions for boolean directive", variants)
        assertTrue("Expected 'yes' in boolean suggestions", "yes" in variants!!)
        assertTrue("Expected 'no' in boolean suggestions", "no" in variants)
    }

    fun testBooleanValueCompletionForAnotherBooleanDirective() {
        // AllowNoIcons is another boolean directive → must also offer yes/no
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nAllowNoIcons=<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected boolean value suggestions for AllowNoIcons", variants)
        assertTrue("Expected 'yes' for AllowNoIcons", "yes" in variants!!)
        assertTrue("Expected 'no' for AllowNoIcons", "no" in variants)
    }

    fun testBooleanValueCompletionNotShownForStringDirective() {
        // AppName is a string directive → must NOT get yes/no suggestions from BooleanValueProvider
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nAppName=<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertFalse("'yes' must not appear in string directive completion", "yes" in variants)
        assertFalse("'no' must not appear in string directive completion", "no" in variants)
    }

    // ── ISPP variable completion ──────────────────────────────────────────────────

    fun testIsppVariableCompletionAfterHash() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n#define OutputDir \"out\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#<caret>}\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected ISPP variable suggestions after {#", variants)
        assertTrue("Expected 'AppVersion' in suggestions", "AppVersion" in variants!!)
        assertTrue("Expected 'OutputDir' in suggestions", "OutputDir" in variants)
    }

    fun testIsppVariableShownInBracePopup() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{<caret>\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected constant suggestions after {", variants)
        assertTrue("Expected '#AppVersion' in { popup", "#AppVersion" in variants!!)
    }

    // ── [Languages] built-in language completion ──────────────────────────────

    fun testLanguageNameCompletionUnquoted() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: <caret>; MessagesFile: \"compiler:Default.isl\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected built-in language name suggestions for unquoted Name:", variants)
        assertTrue("Expected 'english' in language name suggestions", "english" in variants!!)
        assertTrue("Expected 'german' in language name suggestions", "german" in variants)
    }

    fun testLanguageNameCompletionQuoted() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"<caret>\"; MessagesFile: \"compiler:Default.isl\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected built-in language name suggestions inside quoted Name:", variants)
        assertTrue("Expected 'english' in quoted language name suggestions", "english" in variants!!)
        assertTrue("Expected 'german' in quoted language name suggestions", "german" in variants)
    }

    fun testLanguageMessagesFileCompletionQuoted() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"english\"; MessagesFile: \"<caret>\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected MessagesFile suggestions inside quoted MessagesFile:", variants)
        assertTrue(
            "Expected 'compiler:Default.isl' in MessagesFile suggestions",
            "compiler:Default.isl" in variants!!
        )
    }

    fun testLanguageNameNotShownOutsideLanguagesSection() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Files]\nSource: \"<caret>\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertFalse(
            "Language names must not appear as completions outside [Languages] section",
            "english" in variants
        )
    }

    /** Renders the lookup element for [name] and returns the icon the popup would show. */
    private fun iconOf(name: String): javax.swing.Icon? =
        myFixture.lookupElements?.firstOrNull { it.lookupString == name }
            ?.let { LookupElementPresentation().also { p -> it.renderElement(p) }.icon }

    fun testLanguageNameCompletionUsesPerLanguageFlagIcon() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: <caret>; MessagesFile: \"compiler:Default.isl\"\n"
        )
        myFixture.completeBasic()
        val germanIcon = iconOf("german")
        val frenchIcon = iconOf("french")
        assertNotNull("German language suggestion must carry a flag icon", germanIcon)
        assertNotNull("French language suggestion must carry a flag icon", frenchIcon)
        assertEquals(
            "German suggestion must use the German flag icon",
            service<IsLanguageDataService>().fromIssName("german")!!.icon, germanIcon
        )
        assertFalse(
            "Distinct languages must use distinct flag icons, not one shared generic icon",
            germanIcon == frenchIcon
        )
    }

    fun testLanguageMessagesFileCompletionUsesPerLanguageFlagIcon() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"english\"; MessagesFile: \"<caret>\"\n"
        )
        myFixture.completeBasic()
        val germanIcon = iconOf("compiler:Languages\\German.isl")
        assertNotNull("German MessagesFile suggestion must carry a flag icon", germanIcon)
        assertEquals(
            "German MessagesFile suggestion must use the German flag icon",
            service<IsLanguageDataService>().fromIssName("german")!!.icon, germanIcon
        )
    }

    // ── [LangOptions] LanguageID completion ───────────────────────────────────

    /** Renders the lookup element whose inserted text equals [lookupString]. */
    private fun presentationOf(lookupString: String): LookupElementPresentation? =
        myFixture.lookupElements?.firstOrNull { it.lookupString == lookupString }
            ?.let { LookupElementPresentation().also { p -> it.renderElement(p) } }

    fun testLanguageIdCompletionShowsNameFlagAndHexId() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[LangOptions]\nLanguageID=<caret>\n")
        myFixture.completeBasic()
        val german = service<IsLanguageDataService>().entries.first { it.displayName == "German (Germany)" }
        val p = presentationOf(german.id)
        assertNotNull("Expected a LanguageID suggestion inserting ${german.id}", p)
        assertEquals("Label must be the locale name", "German (Germany)", p!!.itemText)
        assertEquals("Grey type text must be the hex id", german.id, p.typeText)
        assertEquals("Icon must be the locale flag", german.icon, p.icon)
    }

    fun testLanguageIdCompletionInsertsHexId() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[LangOptions]\nLanguageID=<caret>\n")
        myFixture.completeBasic()
        val german = service<IsLanguageDataService>().entries.first { it.displayName == "German (Germany)" }
        myFixture.lookup!!.currentItem = myFixture.lookupElements!!.first { it.lookupString == german.id }
        myFixture.type("\n")
        assertTrue(
            "Selecting the German entry must insert its hex id ${german.id}",
            myFixture.editor.document.text.contains("LanguageID=${german.id}")
        )
    }

    fun testLanguageIdCompletionLocalesCarryAFlag() {
        // Every recognised locale now has a flag (generated where none existed before).
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[LangOptions]\nLanguageID=<caret>\n")
        myFixture.completeBasic()
        val arabic = service<IsLanguageDataService>().entries.first { it.displayName.startsWith("Arabic") }
        val p = presentationOf(arabic.id)
        assertNotNull("Expected a suggestion for ${arabic.displayName}", p)
        assertNotNull("Every locale must carry a flag icon", p!!.icon)
        assertFalse("Arabic now has a generated flag, not the globe fallback", AllIcons.General.Web == p.icon)
    }

    fun testLanguageIdCompletionNotOfferedForOtherKey() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[LangOptions]\nDialogFontName=<caret>\n")
        myFixture.completeBasic()
        val strings = myFixture.lookupElementStrings ?: emptyList()
        assertFalse("LanguageID suggestions must not appear for DialogFontName", "\$0409" in strings)
    }

    fun testLanguageIdCompletionNotOfferedOutsideLangOptions() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nLanguageID=<caret>\n")
        myFixture.completeBasic()
        val strings = myFixture.lookupElementStrings ?: emptyList()
        assertFalse("LanguageID suggestions must not appear outside [LangOptions]", "\$0409" in strings)
    }

    // ── [Messages] / [CustomMessages] key completion (i18n prefix + message ids) ──

    fun testMessagesKeyCompletionOffersMessageIds() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Messages]\n<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected message-id suggestions in [Messages]", variants)
        assertTrue("Expected 'WelcomeLabel1' in [Messages] keys", "WelcomeLabel1" in variants!!)
        assertTrue("Expected 'SetupAppTitle' in [Messages] keys", "SetupAppTitle" in variants)
    }

    fun testMessagesKeyCompletionOffersNoPrefixWithoutLanguagesSection() {
        // The prefix references a [Languages] Name; with no [Languages] section, none is offered.
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Messages]\n<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertTrue("Message ids must still be offered", "WelcomeLabel1" in variants)
        assertFalse("No language prefixes without a [Languages] section", "english" in variants)
    }

    fun testMessagesKeyPrefixUsesDeclaredLanguagesWhenPresent() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"en\"; MessagesFile: \"compiler:Default.isl\"\nName: \"de\"; " +
                    "MessagesFile: \"compiler:Languages\\German.isl\"\n[Messages]\n<caret>\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected declared-language prefixes in [Messages]", variants)
        assertTrue("Expected declared 'en' prefix", "en" in variants!!)
        assertTrue("Expected declared 'de' prefix", "de" in variants)
        assertFalse(
            "Built-in languages must not be offered when the file declares its own",
            "german" in variants
        )
    }

    fun testMessagesKeyCompletionAfterPrefixOffersMessageIds() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Messages]\nenglish.Welcome<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected message-id suggestions after a 'lang.' prefix", variants)
        assertTrue("Expected 'WelcomeLabel1' after english. prefix", "WelcomeLabel1" in variants!!)
    }

    fun testMessagesPrefixInsertAddsDot() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n[Messages]\n<caret>\n"
        )
        myFixture.completeBasic()
        myFixture.lookup!!.currentItem = myFixture.lookupElements!!.first { it.lookupString == "english" }
        myFixture.type("\n")
        assertTrue(
            "Selecting a language prefix must insert 'english.'",
            myFixture.editor.document.text.contains("english.")
        )
    }

    fun testCustomMessagesKeyCompletionOffersPrefixButNoPredefinedIds() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n[CustomMessages]\n<caret>\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected language-prefix suggestions in [CustomMessages]", variants)
        assertTrue("Expected declared 'english' prefix in [CustomMessages]", "english" in variants!!)
        assertFalse(
            "[CustomMessages] has no predefined message ids",
            "WelcomeLabel1" in variants
        )
    }

    // ── {cm:…} custom-message value completion ────────────────────────────────

    fun testCmCompletionOffersDeclaredCustomMessages() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[CustomMessages]\nGreeting=Hello\nFarewell=Bye\n[Setup]\nAppComments={cm:<caret>}\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected custom-message suggestions inside {cm:}", variants)
        assertTrue("Expected 'Greeting' in {cm:} suggestions", "Greeting" in variants!!)
        assertTrue("Expected 'Farewell' in {cm:} suggestions", "Farewell" in variants)
    }

    fun testCmCompletionStripsLanguagePrefixOfDeclaration() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[CustomMessages]\nenglish.LaunchProgram=Launch\n[Setup]\nAppComments={cm:<caret>}\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertTrue("Custom-message name must be offered without its lang. prefix", "LaunchProgram" in variants)
        assertFalse("The prefixed form must not be offered", "english.LaunchProgram" in variants)
    }

}

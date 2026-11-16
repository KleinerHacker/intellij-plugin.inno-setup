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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Tests for the escape sequences of Inno Setup values: `{{` (literal `{`), a lone `}`, `""` in quoted values
 * and the `%` escapes — their tokens, their robust parsing (half-written forms must never abort the rest of
 * the file), the warning and quick fix for `{{…}}`, the highlighting colour and the colour settings page entry.
 *
 * Background (ticket #63): `AppId={{GUID}` is the documented way to write a GUID. `{{` is the escape of a
 * literal `{`, a closing `}` is never escaped.
 */
class IsSectionEscapeTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "AppName=MyApp\nAppVersion=0.0.1\n"

    private fun file(content: String): IsScriptFile {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return myFixture.file as IsScriptFile
    }

    private fun parseErrors(content: String): List<PsiErrorElement> =
        PsiTreeUtil.collectElementsOfType(file(content), PsiErrorElement::class.java).toList()

    private fun highlights(content: String) =
        myFixture.run { configureByText(IsScriptFileType.INSTANCE, content); doHighlighting() }

    /** Token types of [text] without whitespace. */
    private fun tokens(text: String): List<IElementType> {
        val lexer = IsSectionLexerAdapter()
        lexer.start(text)
        val result = mutableListOf<IElementType>()
        while (true) {
            val type: IElementType = lexer.tokenType ?: break
            if (type != TokenType.WHITE_SPACE) result += type
            lexer.advance()
        }
        return result
    }

    private fun findIntention(prefix: String): IntentionAction =
        myFixture.availableIntentions.firstOrNull { it.text.startsWith(prefix) }
            ?: error("No intention '$prefix'. Available: ${myFixture.availableIntentions.map { it.text }}")

    // ── Lexer ────────────────────────────────────────────────────────────────

    /**
     * Use case: `AppId={{abc}}` is lexed with the escape `{{` as a token of its own, the name, and two plain
     * closing braces.
     */
    fun testLexerEscapedBraceAndTwoClosingBraces() {
        assertEquals(
            listOf(
                IsSectionTypes.IDENTIFIER, IsSectionTypes.EQ, IsSectionTypes.ESCAPED_LBRACE,
                IsSectionTypes.IDENTIFIER, IsSectionTypes.RBRACE, IsSectionTypes.RBRACE, IsSectionTypes.CRLF,
            ),
            tokens("AppId={{abc}}\n")
        )
    }

    /**
     * Use case: a lone `{{` and a lone `{` at the end of a value are lexed as the escape and as an opening brace.
     */
    fun testLexerHalfWrittenOpeningBraces() {
        assertEquals(IsSectionTypes.ESCAPED_LBRACE, tokens("A={{\n")[2])
        assertEquals(IsSectionTypes.LBRACE, tokens("A={\n")[2])
    }

    /**
     * Use case: a closing `}` inside a value text (`a}b`) is a plain closing brace, no escape is needed.
     */
    fun testLexerLoneClosingBraceInValue() {
        assertTrue(tokens("A=a}b\n").contains(IsSectionTypes.RBRACE))
    }

    /**
     * Use case: `""` inside a quoted string is lexed as one escape token between two string parts.
     */
    fun testLexerEscapedQuoteInString() {
        assertEquals(
            listOf(
                IsSectionTypes.IDENTIFIER, IsSectionTypes.EQ, IsSectionTypes.QUOTE, IsSectionTypes.STRING_PART,
                IsSectionTypes.ESCAPED_QUOTE, IsSectionTypes.STRING_PART, IsSectionTypes.QUOTE, IsSectionTypes.CRLF,
            ),
            tokens("A=\"a\"\"b\"\n")
        )
    }

    /**
     * Use case: an empty string `""` keeps being two quote tokens (no escape), because the first quote opens
     * the string and the second one closes it.
     */
    fun testLexerEmptyStringIsNoEscape() {
        val t = tokens("A=\"\"\n")
        assertFalse(t.contains(IsSectionTypes.ESCAPED_QUOTE))
        assertEquals(2, t.count { it == IsSectionTypes.QUOTE })
    }

    /**
     * Use case: `{{` inside a quoted string is the escape, not the start of a constant.
     */
    fun testLexerEscapedBraceInString() {
        val t = tokens("A=\"{{x\"\n")
        assertTrue(t.contains(IsSectionTypes.ESCAPED_LBRACE))
        assertFalse(t.contains(IsSectionTypes.LBRACE))
    }

    // ── Parser ───────────────────────────────────────────────────────────────

    /**
     * Use case: the ticket — `AppId={{012245-13224-234235}}` followed by `AppName` and `AppVersion`. The file
     * parses without errors and all three directives belong to `[Setup]`.
     */
    fun testTicketCaseParsesWithoutErrorsAndKeepsAllDirectives() {
        val content = "[Setup]\nAppId={{012245-13224-234235}}\n$setupTail"
        assertTrue(parseErrors(content).isEmpty())
        val setup = (myFixture.file as IsScriptFile).findSection("Setup") ?: error("No [Setup]")
        assertEquals(listOf("AppId", "AppName", "AppVersion"), setup.directiveEntryList.map { it.keyText() })
    }

    /**
     * Use case: half-written values (`{`, `{{`, `{{a`, `{{...}`, `{{}}`, `{{a}}b`) must not abort the parsing:
     * no parse error appears and `AppName`/`AppVersion` below stay directives of `[Setup]`.
     */
    fun testHalfWrittenValuesDoNotAbortTheRestOfTheFile() {
        listOf("{", "{{", "{{a", "{{...}", "{{}}", "{{a}}b", "{{a}", "a}b", "{a").forEach { value ->
            val content = "[Setup]\nAppId=$value\n$setupTail"
            assertTrue("Parse errors for '$value'", parseErrors(content).isEmpty())
            val setup = (myFixture.file as IsScriptFile).findSection("Setup") ?: error("No [Setup] for '$value'")
            assertEquals(
                "Directives after '$value'",
                listOf("AppId", "AppName", "AppVersion"), setup.directiveEntryList.map { it.keyText() }
            )
        }
    }

    /**
     * Use case: a half-written value in the middle of a parameter section keeps the following entries and the
     * following section intact.
     */
    fun testHalfWrittenParameterValueKeepsFollowingEntriesAndSections() {
        val content = "[Files]\nSource: \"a.exe\"; DestDir: {\nSource: \"b.exe\"; DestDir: \"{app}\"\n" +
                "[Icons]\nName: \"x\"; Filename: \"y\"\n"
        assertTrue(parseErrors(content).isEmpty())
        val file = myFixture.file as IsScriptFile
        assertEquals(2, file.findSection("Files")!!.parameterEntryList.size)
        assertNotNull(file.findSection("Icons"))
    }

    // ── Annotator, warning and quick fix ─────────────────────────────────────

    /**
     * Use case: the ticket text is accepted without an error and without a "required directive" message, but a
     * warning explains that `{{…}}` leaves a stray `}`.
     */
    fun testTicketCaseHasNoErrorButAWarning() {
        val all = highlights("[Setup]\nAppId={{012245-13224-234235}}\n$setupTail")
        assertTrue(
            "No error expected, got: ${all.filter { it.severity == HighlightSeverity.ERROR }.map { it.description }}",
            all.none { it.severity == HighlightSeverity.ERROR }
        )
        assertTrue(all.none { it.description?.contains("Required directive") == true })
        assertTrue(
            all.any { it.severity == HighlightSeverity.WARNING && it.description?.contains("stray '}'") == true }
        )
    }

    /**
     * Use case: the documented GUID form `{{GUID}` (one closing brace) gets neither warning nor error.
     */
    fun testSingleClosingBraceGuidHasNoMessage() {
        val all = highlights("[Setup]\nAppId={{012245-13224-234235}\n$setupTail")
        assertTrue(
            all.none { it.severity == HighlightSeverity.ERROR || it.description?.contains("stray") == true }
        )
    }

    /**
     * Use case: a lone `}`, a `{{` in the middle of a text and `{app}}` (constant followed by a brace) are
     * accepted by the compiler and therefore stay without message.
     */
    fun testAcceptedFormsHaveNoMessage() {
        listOf("a}b", "a{{b", "{app}}").forEach { value ->
            val all = highlights("[Setup]\nAppComments=$value\n$setupTail")
            assertTrue(
                "Unexpected message for '$value': ${all.map { it.description }}",
                all.none {
                    it.description?.contains("stray") == true || it.description?.contains("Unclosed") == true
                }
            )
        }
    }

    /**
     * Use case: an unclosed constant (`{app` without `}`) in a path is reported as `Unclosed constant`.
     */
    fun testUnclosedConstantIsAnError() {
        val all = highlights("[Setup]\n$setupTail[Files]\nSource: \"a.exe\"; DestDir: {app\n")
        assertTrue(all.any { it.severity == HighlightSeverity.ERROR && it.description?.startsWith("Unclosed constant") == true })
    }

    /**
     * Use case: a `{` in the free text of `[Messages]` is not reported (message texts are no constant context).
     */
    fun testBraceInMessageTextIsNotReported() {
        val all = highlights("[Setup]\n$setupTail[Messages]\nButtonNext=Next {\n")
        assertTrue(all.none { it.description?.startsWith("Unclosed constant") == true })
    }

    /**
     * Use case: the quick fix on the warning turns `{{GUID}}` into `{{GUID}` and changes nothing else.
     */
    fun testQuickFixRemovesTheExtraClosingBrace() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Setup]\nAppId={{abc}<caret>}\n$setupTail"
        )
        myFixture.doHighlighting()
        myFixture.launchAction(findIntention("Use a single '}'"))
        assertEquals("[Setup]\nAppId={{abc}\n$setupTail", myFixture.file.text)
    }

    // ── Highlighting ─────────────────────────────────────────────────────────

    /**
     * Use case: the lexer-level tokens `{{` and `""` are coloured with the escape sequence attribute.
     */
    fun testEscapeTokensUseTheEscapeSequenceKey() {
        val highlighter = IsSectionTokenHighlighter()
        assertTrue(highlighter.getTokenHighlights(IsSectionTypes.ESCAPED_LBRACE).contains(IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE))
        assertTrue(highlighter.getTokenHighlights(IsSectionTypes.ESCAPED_QUOTE).contains(IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE))
    }

    /**
     * Use case: without a user override the escape colour is the IDE's "valid string escape" colour.
     */
    fun testEscapeSequenceFallsBackToValidStringEscape() {
        assertEquals(
            DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE,
            IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE.fallbackAttributeKey
        )
    }

    /**
     * Use case: `%n`, `%1` and `%%` in a `[CustomMessages]` value are coloured as escape sequences.
     */
    fun testMessageEscapesAreHighlightedInMessageSections() {
        val text = "[CustomMessages]\nGreeting=Hello%nWelcome %1 (100%%)\n"
        val coloured = highlights(text)
            .filter { it.forcedTextAttributesKey == IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE }
            .map { text.substring(it.startOffset, it.endOffset) }
        assertEquals(listOf("%n", "%1", "%%"), coloured)
    }

    /**
     * Use case: `%n` in a path value (outside of the message sections) is plain text and stays uncoloured.
     */
    fun testMessageEscapesAreNotHighlightedOutsideMessageSections() {
        val text = "[Setup]\n$setupTail[Files]\nSource: \"a.exe\"; DestDir: \"{app}\\a%nb\"\n"
        assertTrue(highlights(text).none { it.forcedTextAttributesKey == IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE })
    }

    /**
     * Use case: `%7d` inside the argument of a constant is coloured as escape sequence.
     */
    fun testConstantArgumentEscapeIsHighlighted() {
        val text = "[CustomMessages]\nGreeting=Hi\n[Setup]\nAppComments={cm:Greeting,a%7db}\n"
        val coloured = highlights(text)
            .filter { it.forcedTextAttributesKey == IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE }
            .map { text.substring(it.startOffset, it.endOffset) }
        assertEquals(listOf("%7d"), coloured)
    }

    // ── Colour settings page ─────────────────────────────────────────────────

    /**
     * Use case: the colour settings dialog lists "Escape sequence" for the new key and its demo text marks
     * escapes with the matching tag.
     */
    fun testColorSettingsPageListsTheEscapeSequence() {
        val page = IsSectionColorSettingsPage()
        val descriptor = page.attributeDescriptors.firstOrNull { it.displayName == "Escape sequence" }
        assertNotNull("Descriptor 'Escape sequence' missing", descriptor)
        assertSame(IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE, descriptor!!.key)
        assertSame(IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE, page.additionalHighlightingTagToDescriptorMap["esc"])
        assertTrue(page.demoText.contains("<esc>{{</esc>"))
    }

    // ── unescapeIssValue ─────────────────────────────────────────────────────

    /**
     * Use case: quotes, `""` and `{{` are resolved the way the compiler does it, including edge cases.
     */
    fun testUnescapeIssValue() {
        assertEquals("a\"b", "\"a\"\"b\"".unescapeIssValue())
        assertEquals("{x}", "{{x}".unescapeIssValue())
        assertEquals("", "".unescapeIssValue())
        assertEquals("", "\"\"".unescapeIssValue())
        assertEquals("{", "{{".unescapeIssValue())
        assertEquals("\"", "\"\"\"\"".unescapeIssValue())
        assertEquals("{a}} \"q\"", "  {{a}} \"q\" ".unescapeIssValue())
    }
}

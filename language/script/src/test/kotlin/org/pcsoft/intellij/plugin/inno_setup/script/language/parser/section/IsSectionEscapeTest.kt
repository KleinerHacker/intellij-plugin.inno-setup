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
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSyntaxHighlighting
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Tests for the escape sequences of Inno Setup values: `{{` (literal `{`), a lone `}`, `""` in quoted values
 * and the `%` escapes — their tokens, their robust parsing (half-written forms must never abort the rest of
 * the file), the warning and quick fix for `{{…}}`, the highlighting colour, the colour settings page entry and
 * the same escapes inside `#` directives.
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

    /** The texts coloured with the escape sequence attribute, in order of their position. */
    private fun escapeTexts(text: String): List<String> =
        highlights(text)
            .filter { it.forcedTextAttributesKey == IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE }
            .sortedBy { it.startOffset }
            .map { text.substring(it.startOffset, it.endOffset) }

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

    /**
     * Use case: the percent escapes are tokens of their own whether the text is written plain, in double
     * quotes or in single quotes — `%n`, `%%` and `%1` each become one escape token.
     */
    fun testLexerPercentEscapesForEveryWayOfWritingTheText() {
        listOf("Hello%nWorld %1 100%%", "\"Hello%nWorld %1 100%%\"", "'Hello%nWorld %1 100%%'").forEach { value ->
            assertEquals(
                "Percent escapes in $value",
                3, tokens("Greeting=$value\n").count { it == IsSectionTypes.ESCAPED_PERCENT }
            )
        }
    }

    /**
     * Use case: a `%` that does not start an escape (`100%`, `%x`, `%APPDATA%`) stays plain text.
     */
    fun testLexerPercentWithoutEscapeIsPlainText() {
        listOf("A=100%\n", "A=%x\n", "A=\"%APPDATA%\"\n").forEach { text ->
            assertFalse("Escape found in $text", tokens(text).contains(IsSectionTypes.ESCAPED_PERCENT))
        }
    }

    /**
     * Use case: `%7d` inside the argument of a constant is an escape token, in a quoted string and in a plain value.
     */
    fun testLexerPercentEscapeInConstantArgument() {
        assertTrue(tokens("A=\"{cm:X,a%7db}\"\n").contains(IsSectionTypes.ESCAPED_PERCENT))
        assertTrue(tokens("A={cm:X,a%7db}\n").contains(IsSectionTypes.ESCAPED_PERCENT))
    }

    /**
     * Use case: an environment constant such as `{%USERNAME}` does not contain an escape (`%U` is no escape),
     * and `{%name}` is not mistaken for the lower-case `%n` of a message in an upper-case name.
     */
    fun testLexerEnvironmentConstantHasNoUpperCaseEscape() {
        assertFalse(tokens("A={%USERNAME}\n").contains(IsSectionTypes.ESCAPED_PERCENT))
        assertFalse(tokens("A={%NAME}\n").contains(IsSectionTypes.ESCAPED_PERCENT))
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
        listOf("{", "{{", "{{a", "{{...}", "{{}}", "{{a}}b", "{{a}", "a}b", "{a", "%", "%n%", "\"a%\"").forEach { value ->
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

    /**
     * Use case: percent escapes in a quoted parameter value and in a constant argument parse without errors.
     */
    fun testPercentEscapesParseWithoutErrors() {
        val content = "[Run]\nFilename: \"{app}\\a.exe\"; Parameters: \"%1 100%% {cm:X,a%7db}\"\n"
        assertTrue(parseErrors(content).isEmpty())
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
     * Use case: the lexer-level tokens `{{`, `""` and the percent escapes are coloured with the escape sequence
     * attribute by the syntax highlighter itself.
     */
    fun testEscapeTokensUseTheEscapeSequenceKey() {
        val highlighter = IsSectionTokenHighlighter()
        listOf(IsSectionTypes.ESCAPED_LBRACE, IsSectionTypes.ESCAPED_QUOTE, IsSectionTypes.ESCAPED_PERCENT).forEach {
            assertTrue("$it", highlighter.getTokenHighlights(it).contains(IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE))
        }
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
     * Use case: the script and the `#` directives share one colour setting for escapes.
     */
    fun testScriptAndDirectivesShareTheEscapeKey() {
        assertSame(IsPreprocessorSyntaxHighlighting.ESCAPE_SEQUENCE, IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE)
    }

    // ── Escapes inside `#` directives ────────────────────────────────────────

    /**
     * Use case: in a double-quoted ISPP string the escapes `{{`, `""` and `%n` are coloured with the escape
     * sequence attribute.
     */
    fun testDirectiveDoubleQuotedStringEscapesAreHighlighted() {
        val text = "#define S \"a{{b\"\"c%nd\"\n[Setup]\n$setupTail"
        assertEquals(listOf("{{", "\"\"", "%n"), escapeTexts(text))
    }

    /**
     * Use case: in a single-quoted ISPP string the doubled quote `''` and the percent escapes are coloured, and
     * the `{{` is coloured as well.
     */
    fun testDirectiveSingleQuotedStringEscapesAreHighlighted() {
        val text = "#define S 'a''b{{c%1'\n[Setup]\n$setupTail"
        assertEquals(listOf("''", "{{", "%1"), escapeTexts(text))
    }

    /**
     * Use case: a `{{` in plain (unquoted) directive text is coloured as escape.
     */
    fun testDirectivePlainTextEscapeIsHighlighted() {
        val text = "#define S {{abc\n[Setup]\n$setupTail"
        assertEquals(listOf("{{"), escapeTexts(text))
    }

    /**
     * Use case: a lone apostrophe in free directive text (`#error Don't`) is no string; nothing is coloured.
     */
    fun testDirectiveApostropheIsNoString() {
        val text = "#error Don't do 100%1 it ''\n[Setup]\n$setupTail"
        assertEquals(emptyList<String>(), escapeTexts(text))
    }

    /**
     * Use case: `{{…}}` with two closing braces in a directive is warned about, like in a script value.
     */
    fun testDirectiveDoubleClosedBraceIsWarnedAbout() {
        val all = highlights("#define Id \"{{abc}}\"\n[Setup]\n$setupTail")
        assertTrue(all.any { it.severity == HighlightSeverity.WARNING && it.description?.contains("stray '}'") == true })
    }

    /**
     * Use case: the quick fix on the directive warning removes the extra closing brace in the host file.
     */
    fun testDirectiveQuickFixRemovesTheExtraClosingBrace() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Id \"{{abc}<caret>}\"\n[Setup]\n$setupTail"
        )
        myFixture.doHighlighting()
        myFixture.launchAction(findIntention("Use a single '}'"))
        // The fix runs in the injected ISPP fragment, so the fixture file is the fragment: read the host file.
        val host = InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file)
        assertEquals("#define Id \"{{abc}\"\n[Setup]\n$setupTail", host.text)
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

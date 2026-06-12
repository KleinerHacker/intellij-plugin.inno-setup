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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsService

/**
 * Comprehensive tests for IsSectionAnnotator — one test per defined case.
 *
 * Existing tests in IsSectionAnnotatorPreprocessorTest and IsSectionHighlightingTest cover ISPP constant
 * and section-name highlighting; this file covers the remaining annotator branches.
 */
class IsSectionAnnotatorTest : BasePlatformTestCase() {

    private fun highlights(content: String) =
        myFixture.run { configureByText(IsScriptFileType.INSTANCE, content); doHighlighting() }

    // Minimal valid file that can be enriched per test
    private val VALID_SETUP = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun withMinVersion(version: String?, block: () -> Unit) {
        val service = IsSettingsService.getInstance()
        val prev = service.state.minInnoVersion
        service.state.minInnoVersion = version
        try {
            block()
        } finally {
            service.state.minInnoVersion = prev
        }
    }

    // ── {cm:…} italic + language prefix ───────────────────────────────────────

    fun testCmKeywordIsItalic() {
        val text = "[CustomMessages]\nGreeting=Hi\n[Setup]\nAppComments={cm:Greeting}\n"
        myFixture.configureByText(IsScriptFileType.INSTANCE, text)
        val cmOffset = text.indexOf("cm:")
        val italic = myFixture.doHighlighting().any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.CUSTOM_MESSAGE_PREFIX &&
                    it.startOffset <= cmOffset && it.endOffset >= cmOffset + 2
        }
        assertTrue("The cm keyword in {cm:…} must be highlighted italic", italic)
    }

    fun testUnknownLanguagePrefixProducesError() {
        val text = "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n" +
                "[Messages]\nde.WelcomeLabel1=Willkommen\n"
        val hit = highlights(text).any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown language prefix", ignoreCase = true) == true
        }
        assertTrue("An undeclared lang. prefix must produce an ERROR", hit)
    }

    fun testKnownLanguagePrefixProducesNoError() {
        val text = "[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n" +
                "[Messages]\nenglish.WelcomeLabel1=Welcome\n"
        val hit = highlights(text).any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown language prefix", ignoreCase = true) == true
        }
        assertFalse("A declared lang. prefix must not be flagged", hit)
    }

    // ── Wrong key/value separator (':' vs '=') ────────────────────────────────

    private fun separatorError(content: String) = highlights(content).any {
        it.severity == HighlightSeverity.ERROR &&
                it.description?.contains("separates key and value", ignoreCase = true) == true
    }

    /** True when a wrong-separator ERROR is reported exactly over the separator character at [sepOffset]. */
    private fun separatorErrorOnChar(content: String, sepOffset: Int) = highlights(content).any {
        it.severity == HighlightSeverity.ERROR &&
                it.description?.contains("separates key and value", ignoreCase = true) == true &&
                it.startOffset == sepOffset && it.endOffset == sepOffset + 1
    }

    fun testColonInDirectiveSectionProducesError() {
        // [CustomMessages] is a directive section — entries must use '=', not ':'.
        val text = "[CustomMessages]\ndemo: \"vgff\"\n"
        assertTrue(
            "the wrong ':' in a [CustomMessages] entry must be underlined in red",
            separatorErrorOnChar(text, text.indexOf("demo: ") + 4) // the ':' itself
        )
    }

    fun testColonInSetupSectionProducesError() {
        val text = "[Setup]\nAppName: Test\nAppVersion=1.0\n"
        assertTrue(
            "the wrong ':' in a [Setup] entry must be underlined in red",
            separatorErrorOnChar(text, text.indexOf("AppName") + "AppName".length) // the ':' itself
        )
    }

    fun testEqualsInDirectiveSectionProducesNoSeparatorError() {
        // Canonical '=' form must not be flagged.
        assertFalse(
            "'=' in a [CustomMessages] entry is correct and must not be flagged",
            separatorError("[CustomMessages]\ndemo=vgff\n")
        )
    }

    fun testEqualsInParameterSectionProducesError() {
        // [Files] is a parameter section — entries must use ':', not '='.
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\n[Files]\nSource=app.exe\n"
        assertTrue(
            "the wrong '=' in a [Files] entry must be underlined in red",
            separatorErrorOnChar(text, text.indexOf("Source") + "Source".length) // the '=' itself
        )
    }

    fun testColonInParameterSectionProducesNoSeparatorError() {
        // Canonical ':' form must not be flagged.
        assertFalse(
            "':' in a [Files] entry is correct and must not be flagged",
            separatorError("[Setup]\nAppName=Test\nAppVersion=1.0\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n")
        )
    }

    // ── File level: required sections ─────────────────────────────────────────

    fun testFileLevelErrorWhenSetupSectionMissing() {
        val all = highlights("[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n")
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Required section", ignoreCase = true) == true
        }
        assertTrue("Missing [Setup] must produce a file-level 'Required section' ERROR", hit)
    }

    // ── File level: [Code] ordering ───────────────────────────────────────────

    fun testCodeSectionNotLastProducesError() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\n\n[Code]\n\n[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("[Code] must be the last section") == true
        }
        assertTrue("[Code] placed before [Files] must produce 'must be the last section' ERROR", hit)
    }

    fun testSectionAfterCodeProducesError() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\n\n[Code]\n\n[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("appears after [Code]") == true
        }
        assertTrue("[Files] after [Code] must produce 'appears after [Code]' ERROR", hit)
    }

    // ── Section level: required directives missing ────────────────────────────

    fun testSetupSectionMissingRequiredDirectivesProducesError() {
        val all = highlights("[Setup]\n; intentionally empty\n")
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Required directive", ignoreCase = true) == true
        }
        assertTrue("[Setup] with no directives must produce 'Required directive' ERROR", hit)
    }

    // ── Entry level: required parameters missing ──────────────────────────────

    fun testFilesEntryMissingDestDirProducesError() {
        // Source is present but DestDir is missing → required param error
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Required parameter", ignoreCase = true) == true
        }
        assertTrue("[Files] entry without DestDir must produce 'Required parameter' ERROR", hit)
    }

    fun testFilesEntryWithAllRequiredParamsProducesNoEntryError() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        val entryErrors = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Required parameter", ignoreCase = true) == true
        }
        assertTrue(
            "Complete [Files] entry must not produce 'Required parameter' ERROR",
            entryErrors.isEmpty()
        )
    }

    // ── Directive key: unknown ────────────────────────────────────────────────

    fun testUnknownDirectiveKeyProducesError() {
        val text = VALID_SETUP + "NonExistentDirective=somevalue\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE
        }
        assertTrue("Unknown directive key must produce UNKNOWN_REFERENCE ERROR", hit)
    }

    // ── Param key: unknown ────────────────────────────────────────────────────

    fun testUnknownParamKeyProducesError() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; NonExistentKey: val\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE
        }
        assertTrue("Unknown param key must produce UNKNOWN_REFERENCE ERROR", hit)
    }

    // ── Param value — flag type ───────────────────────────────────────────────

    fun testValidFlagHighlightedAsFlagColor() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: ignoreversion\n"
        val fileText = myFixture.run {
            configureByText(IsScriptFileType.INSTANCE, text); file.text
        }
        val all = myFixture.doHighlighting()
        val flagOffset = fileText.lastIndexOf("ignoreversion")
        val hit = all.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.FLAG &&
                    it.startOffset == flagOffset
        }
        assertTrue("Valid flag 'ignoreversion' must be highlighted with FLAG attribute", hit)
    }

    fun testUnknownFlagProducesError() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: noSuchFlag\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown flag", ignoreCase = true) == true &&
                    it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE
        }
        assertTrue("Unknown flag must produce 'Unknown flag' ERROR with UNKNOWN_REFERENCE", hit)
    }

    fun testConflictingFlagsWithErrorSeverityProducesTwoErrors() {
        // deleteafterinstall and restartreplace conflict at ERROR level in [Files].
        // Using these instead of 32bit/64bit because flag names starting with a digit
        // are not tokenized as IDENTIFIER by the ISS lexer.
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: deleteafterinstall restartreplace\n"
        val all = highlights(text)
        val conflicts = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Conflicting flags", ignoreCase = true) == true
        }
        assertEquals(
            "Conflicting flags 'deleteafterinstall'/'restartreplace' must produce exactly 2 ERROR annotations",
            2, conflicts.size
        )
    }

    fun testConflictingFlagsWithWarningSeverityProducesTwoWarnings() {
        // createvalueifdoesntexist has no effect when deletevalue is also specified
        // ('ignored' type) — conflict reported at WARNING level in [Registry].
        val text =
            VALID_SETUP + "\n[Registry]\nRoot: HKLM; Subkey: \"Software\\Test\"; Flags: createvalueifdoesntexist deletevalue\n"
        val all = highlights(text)
        val conflicts = all.filter {
            it.severity == HighlightSeverity.WARNING &&
                    it.description?.contains("Conflicting flags", ignoreCase = true) == true
        }
        assertEquals(
            "Conflicting flags 'createvalueifdoesntexist'/'deletevalue' must produce exactly 2 WARNING annotations",
            2, conflicts.size
        )
    }

    // ── Param value — required flags ──────────────────────────────────────────

    fun testRequiredFlagMissingProducesError() {
        // extractarchive requires external and ignoreversion.
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"a.zip\"; DestDir: \"{app}\"; Flags: extractarchive\n"
        val errors = highlights(text).filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("requires", ignoreCase = true) == true
        }
        assertEquals("A flag missing its required flags must produce exactly one ERROR", 1, errors.size)
    }

    fun testRequiredFlagsPresentProducesNoError() {
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"a.zip\"; DestDir: \"{app}\"; Flags: extractarchive external ignoreversion\n"
        val errors = highlights(text).filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("requires", ignoreCase = true) == true
        }
        assertEquals("When all required flags are present there must be no requires ERROR", 0, errors.size)
    }

    // ── Param value — redundant flags ─────────────────────────────────────────

    fun testRedundantFlagProducesSingleWeakWarning() {
        // 'external' makes 'nocompression' redundant (no effect when combined).
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: external nocompression\n"
        val all = highlights(text)
        val redundant = all.filter {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("redundant", ignoreCase = true) == true
        }
        assertEquals(
            "A redundant flag must produce exactly one WEAK_WARNING (only on the implied flag)",
            1, redundant.size
        )
    }

    fun testRedundantFlagIsHighlightedAsUnused() {
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: external nocompression\n"
        val fileText = myFixture.run { configureByText(IsScriptFileType.INSTANCE, text); file.text }
        val all = myFixture.doHighlighting()
        val redundantOffset = fileText.lastIndexOf("nocompression")
        val hit = all.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNUSED &&
                    it.startOffset == redundantOffset
        }
        assertTrue("The redundant flag 'nocompression' must use the UNUSED text attribute", hit)
    }

    fun testRedundantFlagOnlyMarksImpliedFlagNotTheImplyingOne() {
        // The implying flag 'external' must stay a normal FLAG, not be greyed out.
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: external nocompression\n"
        val fileText = myFixture.run { configureByText(IsScriptFileType.INSTANCE, text); file.text }
        val all = myFixture.doHighlighting()
        val externalOffset = fileText.lastIndexOf("external")
        val markedUnused = all.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNUSED &&
                    it.startOffset == externalOffset
        }
        assertFalse("The implying flag 'external' must not be greyed out as UNUSED", markedUnused)
    }

    fun testExtractArchiveMakesReplaceSameVersionRedundant() {
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"app.zip\"; DestDir: \"{app}\"; Flags: extractarchive replacesameversion\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("redundant", ignoreCase = true) == true
        }
        assertTrue("'replacesameversion' must be flagged redundant when combined with 'extractarchive'", hit)
    }

    fun testFlagWithoutImplyingCounterpartProducesNoRedundantWarning() {
        // 'nocompression' alone (no 'external') must not be flagged redundant.
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: nocompression\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("redundant", ignoreCase = true) == true
        }
        assertFalse("'nocompression' without 'external' must not be flagged redundant", hit)
    }

    // ── Param value — native type validation ──────────────────────────────────

    fun testInvalidBooleanValueProducesError() {
        // CloseApplications is a boolean directive in [Setup]
        val text = VALID_SETUP + "CloseApplications=maybe\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Expected type", ignoreCase = true) == true &&
                    it.description?.contains("boolean", ignoreCase = true) == true
        }
        assertTrue("Invalid boolean value 'maybe' for CloseApplications must produce type ERROR", hit)
    }

    fun testValidBooleanValueProducesNoTypeError() {
        val text = VALID_SETUP + "CloseApplications=yes\n"
        val all = highlights(text)
        val typeErrors = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Expected type", ignoreCase = true) == true
        }
        assertTrue(
            "Valid boolean value 'yes' for CloseApplications must not produce type ERROR",
            typeErrors.isEmpty()
        )
    }

    fun testInvalidIntegerValueProducesError() {
        // Components.ExtraDiskSpaceRequired is an integer param
        val text =
            VALID_SETUP + "\n[Components]\nName: core; Description: \"Core\"; ExtraDiskSpaceRequired: notanumber\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Expected type", ignoreCase = true) == true &&
                    it.description?.contains("integer", ignoreCase = true) == true
        }
        assertTrue("Invalid integer value 'notanumber' for ExtraDiskSpaceRequired must produce type ERROR", hit)
    }

    fun testValidIntegerValueProducesNoTypeError() {
        val text = VALID_SETUP + "\n[Components]\nName: core; Description: \"Core\"; ExtraDiskSpaceRequired: 1024\n"
        val all = highlights(text)
        val typeErrors = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Expected type", ignoreCase = true) == true
        }
        assertTrue(
            "Valid integer value '1024' for ExtraDiskSpaceRequired must not produce type ERROR",
            typeErrors.isEmpty()
        )
    }

    // ── Param value — string type is intentionally not numeric-validated ──────

    fun testNumericValueForStringDirectiveProducesNoTypeError() {
        // AppVersion is a string directive; "1.0" is numeric but a perfectly valid
        // version string. It must NOT be flagged as a type error (no false positive).
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val all = highlights(text)
        val typeErrors = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Expected type", ignoreCase = true) == true
        }
        assertTrue(
            "Numeric value '1.0' for string directive AppVersion must not produce a type ERROR",
            typeErrors.isEmpty()
        )
    }

    fun testNumericValueForStringParameterProducesNoTypeError() {
        // Description is a string parameter; a bare number must not be flagged.
        val text = VALID_SETUP + "\n[Components]\nName: core; Description: 123\n"
        val all = highlights(text)
        val typeErrors = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Expected type", ignoreCase = true) == true
        }
        assertTrue(
            "Numeric value '123' for string parameter Description must not produce a type ERROR",
            typeErrors.isEmpty()
        )
    }

    fun testStringValueForIntegerParameterProducesError() {
        // The "vice versa" direction: a non-numeric string in an integer field IS flagged.
        val text = VALID_SETUP + "\n[Components]\nName: core; Description: \"Core\"; ExtraDiskSpaceRequired: \"lots\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Expected type", ignoreCase = true) == true &&
                    it.description?.contains("integer", ignoreCase = true) == true
        }
        assertTrue("String value '\"lots\"' for integer ExtraDiskSpaceRequired must produce a type ERROR", hit)
    }

    // ── Constant: known builtin ───────────────────────────────────────────────

    fun testKnownBuiltinConstantHighlightedAsReference() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val fileText = myFixture.run {
            configureByText(IsScriptFileType.INSTANCE, text); file.text
        }
        val all = myFixture.doHighlighting()
        val appOffset = fileText.lastIndexOf("{app}")
        val hit = all.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.REFERENCE &&
                    it.startOffset == appOffset
        }
        assertTrue("Known builtin constant '{app}' must be highlighted with REFERENCE attribute", hit)
    }

    fun testUnknownBuiltinConstantProducesError() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{unknownconstant}\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown constant", ignoreCase = true) == true
        }
        assertTrue("Unknown constant '{unknownconstant}' must produce 'Unknown constant' ERROR", hit)
    }

    // ── Trailing semicolon ────────────────────────────────────────────────────

    fun testTrailingSemicolonProducesWeakWarning() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\";\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("Trailing semicolon", ignoreCase = true) == true
        }
        assertTrue("Trailing ';' must produce a 'Trailing semicolon is optional' WEAK_WARNING", hit)
    }

    fun testTrailingSemicolonIsHighlightedAsUnused() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\";\n"
        val fileText = myFixture.run { configureByText(IsScriptFileType.INSTANCE, text); file.text }
        val all = myFixture.doHighlighting()
        val semiOffset = fileText.lastIndexOf(";")
        val hit = all.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNUSED &&
                    it.startOffset == semiOffset
        }
        assertTrue("Trailing ';' must use the UNUSED text attribute", hit)
    }

    fun testNonTrailingSemicolonProducesNoUnusedWarning() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("Trailing semicolon", ignoreCase = true) == true
        }
        assertFalse("No trailing ';' must not produce a 'Trailing semicolon' warning", hit)
    }

    // ── Empty sections ────────────────────────────────────────────────────────

    fun testEmptySectionProducesWeakWarning() {
        val text = VALID_SETUP + "\n[Registry]\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("Empty section", ignoreCase = true) == true
        }
        assertTrue("Empty [Registry] section must produce an 'Empty section' WEAK_WARNING", hit)
    }

    fun testEmptySectionIsHighlightedAsUnused() {
        val text = VALID_SETUP + "\n[Registry]\n"
        val fileText = myFixture.run { configureByText(IsScriptFileType.INSTANCE, text); file.text }
        val all = myFixture.doHighlighting()
        val nameOffset = fileText.indexOf("Registry")
        val hit = all.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNUSED &&
                    it.startOffset == nameOffset
        }
        assertTrue("Empty section name must use the UNUSED text attribute", hit)
    }

    fun testNonEmptySectionProducesNoEmptySectionWarning() {
        val text = VALID_SETUP + "\n[Registry]\nRoot: HKLM; Subkey: Software\\MyApp\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("Empty section", ignoreCase = true) == true
        }
        assertFalse("Non-empty [Registry] section must not produce an 'Empty section' warning", hit)
    }

    fun testCodeSectionIsNeverFlaggedAsEmpty() {
        // [Code] is free-form Pascal — even a structurally empty [Code] must not warn.
        val text = VALID_SETUP + "\n[Code]\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("Empty section", ignoreCase = true) == true
        }
        assertFalse("[Code] section must never produce an 'Empty section' warning", hit)
    }

    // ── Preprocessor directive keyword ────────────────────────────────────────

    fun testPreprocessorIncludeKeywordHighlighted() {
        val text = "#include \"other.iss\"\n" + VALID_SETUP
        val fileText = myFixture.run {
            configureByText(IsScriptFileType.INSTANCE, text); file.text
        }
        val all = myFixture.doHighlighting()
        val hit = all.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE &&
                    fileText.substring(it.startOffset, it.endOffset) == "#include"
        }
        assertTrue("'#include' directive must be highlighted with PREPROCESSOR_DIRECTIVE", hit)
    }

    // ── Reference type value ──────────────────────────────────────────────────

    // ── Version annotations ───────────────────────────────────────────────────

    fun testDirectiveTooNewProducesWarning() {
        // ArchiveExtraction has since="6.5"; targeting 6.0 → WARNING
        withMinVersion("6.0") {
            val text = VALID_SETUP + "ArchiveExtraction=full\n"
            val all = highlights(text)
            val hit = all.any {
                it.severity == HighlightSeverity.WARNING &&
                        it.description?.contains("requires Inno Setup", ignoreCase = true) == true
            }
            assertTrue("Directive 'ArchiveExtraction' (since 6.5) used with minVersion=6.0 must produce a WARNING", hit)
        }
    }

    fun testDirectiveSatisfiesMinVersionProducesNoVersionWarning() {
        // ArchiveExtraction has since="6.5"; targeting 6.5 → no version warning
        withMinVersion("6.5") {
            val text = VALID_SETUP + "ArchiveExtraction=full\n"
            val all = highlights(text)
            val hit = all.any {
                it.severity == HighlightSeverity.WARNING &&
                        it.description?.contains("requires Inno Setup", ignoreCase = true) == true
            }
            assertFalse(
                "Directive 'ArchiveExtraction' (since 6.5) used with minVersion=6.5 must NOT produce a version WARNING",
                hit
            )
        }
    }

    fun testFlagTooNewProducesWarning() {
        // signcheck flag has since="6.4"; targeting 6.0 → WARNING
        withMinVersion("6.0") {
            val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: signcheck\n"
            val all = highlights(text)
            val hit = all.any {
                it.severity == HighlightSeverity.WARNING &&
                        it.description?.contains("requires Inno Setup", ignoreCase = true) == true
            }
            assertTrue("Flag 'signcheck' (since 6.4) used with minVersion=6.0 must produce a WARNING", hit)
        }
    }

    fun testConstantRemovedProducesError() {
        // {hwnd} has until="6.4"; targeting 6.4 → ERROR
        withMinVersion("6.4") {
            val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{hwnd}\"\n"
            val all = highlights(text)
            val hit = all.any {
                it.severity == HighlightSeverity.ERROR &&
                        it.description?.contains("removed in Inno Setup", ignoreCase = true) == true
            }
            assertTrue("Constant '{hwnd}' (until 6.4) used with minVersion=6.4 must produce a 'removed' ERROR", hit)
        }
    }

    fun testConstantNotRemovedYetProducesNoRemovedError() {
        // {hwnd} has until="6.4"; targeting 6.3 → no removed error (just deprecated)
        withMinVersion("6.3") {
            val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{hwnd}\"\n"
            val all = highlights(text)
            val hit = all.any {
                it.severity == HighlightSeverity.ERROR &&
                        it.description?.contains("removed in Inno Setup", ignoreCase = true) == true
            }
            assertFalse(
                "Constant '{hwnd}' (until 6.4) used with minVersion=6.3 must NOT produce a 'removed' ERROR",
                hit
            )
        }
    }

    fun testNoMinVersionConfiguredProducesNoVersionAnnotations() {
        // Without configured minVersion no version warnings are emitted
        withMinVersion(null) {
            val text = VALID_SETUP + "ArchiveExtraction=full\n"
            val all = highlights(text)
            val hit = all.any {
                (it.severity == HighlightSeverity.WARNING || it.severity == HighlightSeverity.ERROR) &&
                        it.description?.contains("requires Inno Setup", ignoreCase = true) == true
            }
            assertFalse("No minVersion configured must not produce any version-related annotations", hit)
        }
    }

    fun testTypesReferenceValueHighlightedAsReference() {
        val text = VALID_SETUP +
                "\n[Types]\nName: full; Description: \"Full\"\n" +
                "\n[Components]\nName: core; Description: \"Core\"; Types: full\n"
        val fileText = myFixture.run {
            configureByText(IsScriptFileType.INSTANCE, text); file.text
        }
        val all = myFixture.doHighlighting()
        val offset = fileText.lastIndexOf("full")
        val hit = all.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.REFERENCE &&
                    it.startOffset == offset
        }
        assertTrue("'full' in Types: parameter must be highlighted with REFERENCE attribute", hit)
    }

    // ── Pascal-hex integers ───────────────────────────────────────────────────

    private fun hasTypeError(all: List<com.intellij.codeInsight.daemon.impl.HighlightInfo>) =
        all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Expected type", ignoreCase = true) == true
        }

    fun testHexIntegerValueAcceptedForIntegerParameter() {
        // $1000 is a valid Pascal hex integer for the integer param ExtraDiskSpaceRequired.
        val text = VALID_SETUP + "\n[Components]\nName: core; Description: \"Core\"; ExtraDiskSpaceRequired: \$1000\n"
        assertFalse(
            "Pascal hex '\$1000' must be accepted for an integer parameter (no type error)",
            hasTypeError(highlights(text))
        )
    }

    fun testInvalidHexIntegerValueStillProducesError() {
        // $GG is not valid hex → still a type error.
        val text = VALID_SETUP + "\n[Components]\nName: core; Description: \"Core\"; ExtraDiskSpaceRequired: \$GG\n"
        assertTrue(
            "Non-hex '\$GG' for an integer parameter must still produce a type ERROR",
            hasTypeError(highlights(text))
        )
    }

    // ── [LangOptions] LanguageID validation ───────────────────────────────────

    private fun hasUnknownLcidWarning(all: List<com.intellij.codeInsight.daemon.impl.HighlightInfo>) =
        all.any {
            it.severity == HighlightSeverity.WARNING &&
                    it.description?.contains("Unknown Windows language identifier", ignoreCase = true) == true
        }

    fun testLangOptionsValidLanguageIdProducesNoWarning() {
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=\$0409\n"
        val all = highlights(text)
        assertFalse("Valid LCID \$0409 must not warn", hasUnknownLcidWarning(all))
        assertFalse("Valid hex LCID must not be a type error", hasTypeError(all))
    }

    fun testLangOptionsZeroLanguageIdProducesNoWarning() {
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=0\n"
        assertFalse("LanguageID=0 must be allowed without warning", hasUnknownLcidWarning(highlights(text)))
    }

    fun testLangOptionsUnknownLanguageIdProducesWarning() {
        // $9999 is a well-formed hex integer but not an assigned LCID.
        val text = VALID_SETUP + "\n[LangOptions]\nLanguageID=\$9999\n"
        assertTrue(
            "An unassigned LCID \$9999 must produce an 'Unknown Windows language identifier' WARNING",
            hasUnknownLcidWarning(highlights(text))
        )
    }

    fun testLanguageIdWarningOnlyAppliesToLangOptions() {
        // Same key name in a different section must not be LCID-validated.
        val text = VALID_SETUP + "\n[Setup]\nLanguageID=\$9999\n"
        assertFalse(
            "LanguageID outside [LangOptions] must not be LCID-validated",
            hasUnknownLcidWarning(highlights(text))
        )
    }

    // ── MessagesFile validation ───────────────────────────────────────────────

    private fun withInstallPath(path: String?, block: () -> Unit) {
        val service = IsSettingsService.getInstance()
        val prev = service.state.installationPath
        service.state.installationPath = path
        try {
            block()
        } finally {
            service.state.installationPath = prev
        }
    }

    fun testMessagesFileCompilerPrefixWithoutInstallPathProducesWarning() {
        withInstallPath(null) {
            val text = VALID_SETUP + "\n[Languages]\nName: english; MessagesFile: \"compiler:Default.isl\"\n"
            val all = highlights(text)
            val hit = all.any {
                it.severity == HighlightSeverity.WARNING &&
                        it.description?.contains("installation path not configured", ignoreCase = true) == true
            }
            assertTrue(
                "compiler: prefix without configured installation path must produce a WARNING",
                hit
            )
        }
    }

    fun testMessagesFileMissingAbsolutePathProducesError() {
        val nonExistent = "/nonexistent_xzy_dir_9999/lang.isl"
        val text = VALID_SETUP + "\n[Languages]\nName: english; MessagesFile: \"$nonExistent\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("not found", ignoreCase = true) == true
        }
        assertTrue("Absolute path to non-existent ISL must produce 'not found' ERROR", hit)
    }

    fun testMessagesFileWithUnresolvableIsppConstantProducesNoAnnotation() {
        // {#UnknownDefine} cannot be resolved → should produce no annotation (not prematurely flagged)
        val text = VALID_SETUP + "\n[Languages]\nName: english; MessagesFile: \"{#UnknownDefine}.isl\"\n"
        val all = highlights(text)
        val isl = all.filter {
            (it.severity == HighlightSeverity.ERROR || it.severity == HighlightSeverity.WARNING) &&
                    it.description?.contains("ISL", ignoreCase = true) == true
        }
        assertTrue("Unresolvable {#…} in MessagesFile must produce no ISL-related annotation", isl.isEmpty())
    }

    // ── {%ENV} constant validation ─────────────────────────────────────────────

    fun testKnownEnvVarConstantProducesNoError() {
        System.getenv("PATH") ?: return  // skip if not available
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{%PATH}\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("environment variable", ignoreCase = true) == true
        }
        assertFalse("Known env var {%PATH} must not produce an ERROR", hit)
    }

    fun testUnknownEnvVarWithoutDefaultProducesError() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{%GIBTSNICHT_XYZ_9999}\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown environment variable", ignoreCase = true) == true
        }
        assertTrue("Unknown env var without default must produce an 'Unknown environment variable' ERROR", hit)
    }

    fun testUnknownEnvVarWithDefaultProducesNoError() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{%GIBTSNICHT_XYZ_9999|fallback}\"\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("environment variable", ignoreCase = true) == true
        }
        assertFalse("Unknown env var with default must not produce an ERROR", hit)
    }

    // ── [CustomMessages] — free-form keys must not be flagged ─────────────────

    fun testCustomMessagesUserKeyNotFlaggedUnknown() {
        val text = VALID_SETUP + "\n[CustomMessages]\nCreateDesktopIcon=Create a &desktop icon\nLaunchProgram=Launch %1\n"
        val all = highlights(text)
        val unknown = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE
        }
        assertTrue(
            "User-defined [CustomMessages] keys must not be flagged as UNKNOWN_REFERENCE: $unknown",
            unknown.isEmpty()
        )
    }

    fun testCustomMessagesLanguagePrefixedKeyNotFlaggedUnknown() {
        // e.g. english.CreateDesktopIcon=... is valid in an internationalized section
        val text = VALID_SETUP + "\n[Languages]\nName: \"english\"; MessagesFile: \"compiler:Default.isl\"\n" +
                "[CustomMessages]\nenglish.CreateDesktopIcon=Create a &desktop icon\n"
        val all = highlights(text)
        val unknown = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown", ignoreCase = true) == true &&
                    it.description?.contains("CustomMessages", ignoreCase = true) != true  // exclude {cm:} errors
        }
        assertTrue(
            "Language-prefixed [CustomMessages] key must not produce an UNKNOWN_REFERENCE ERROR: $unknown",
            unknown.isEmpty()
        )
    }

    // ── {cm:…} annotation and reference ──────────────────────────────────────

    fun testCmReferenceToDefinedMessageProducesNoError() {
        val text = VALID_SETUP + "\n[CustomMessages]\nGreeting=Hello\n" +
                "[Setup]\nAppComments={cm:Greeting}\n"
        val all = highlights(text)
        val cmErrors = all.filter {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown custom message", ignoreCase = true) == true
        }
        assertTrue("{cm:Greeting} pointing to a defined message must not produce an error", cmErrors.isEmpty())
    }

    fun testCmReferenceToUndefinedMessageProducesError() {
        val text = VALID_SETUP + "\n[CustomMessages]\nGreeting=Hello\n" +
                "[Setup]\nAppComments={cm:NonExistent}\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown custom message", ignoreCase = true) == true
        }
        assertTrue("{cm:NonExistent} must produce an 'Unknown custom message' ERROR", hit)
    }

    fun testCmReferenceResolvesViaGetReferences() {
        val text = VALID_SETUP + "\n[CustomMessages]\nMyMsg=Hello\n[Setup]\nAppComments={cm:MyMsg}\n"
        myFixture.configureByText(IsScriptFileType.INSTANCE, text)
        // Find the cm: constant body offset to place the caret on the message name
        val fileText = myFixture.file.text
        val msgNameOffset = fileText.indexOf("cm:MyMsg") + "cm:".length
        val ref = myFixture.file.findReferenceAt(msgNameOffset)
        assertNotNull("A PsiReference must exist on the message name in {cm:MyMsg}", ref)
        val resolved = ref?.resolve()
        assertNotNull("The {cm:MyMsg} reference must resolve to the [CustomMessages] declaration", resolved)
    }
}

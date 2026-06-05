package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Comprehensive tests for IsiAnnotator — one test per defined case.
 *
 * Existing tests in IsiAnnotatorIsppTest and IsiHighlightingTest cover ISPP constant
 * and section-name highlighting; this file covers the remaining annotator branches.
 */
class IsiAnnotatorTest : BasePlatformTestCase() {

    private fun highlights(content: String) =
        myFixture.run { configureByText(IssFileType.INSTANCE, content); doHighlighting() }

    // Minimal valid file that can be enriched per test
    private val VALID_SETUP = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

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
                    it.forcedTextAttributesKey == IsiAnnotatorHighlighting.UNKNOWN_REFERENCE
        }
        assertTrue("Unknown directive key must produce UNKNOWN_REFERENCE ERROR", hit)
    }

    // ── Param key: unknown ────────────────────────────────────────────────────

    fun testUnknownParamKeyProducesError() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; NonExistentKey: val\n"
        val all = highlights(text)
        val hit = all.any {
            it.severity == HighlightSeverity.ERROR &&
                    it.forcedTextAttributesKey == IsiAnnotatorHighlighting.UNKNOWN_REFERENCE
        }
        assertTrue("Unknown param key must produce UNKNOWN_REFERENCE ERROR", hit)
    }

    // ── Param value — flag type ───────────────────────────────────────────────

    fun testValidFlagHighlightedAsFlagColor() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: ignoreversion\n"
        val fileText = myFixture.run {
            configureByText(IssFileType.INSTANCE, text); file.text
        }
        val all = myFixture.doHighlighting()
        val flagOffset = fileText.lastIndexOf("ignoreversion")
        val hit = all.any {
            it.forcedTextAttributesKey == IsiAnnotatorHighlighting.FLAG &&
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
                    it.forcedTextAttributesKey == IsiAnnotatorHighlighting.UNKNOWN_REFERENCE
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
        // ignoreversion and comparetimestamp conflict at WARNING level
        val text =
            VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: ignoreversion comparetimestamp\n"
        val all = highlights(text)
        val conflicts = all.filter {
            it.severity == HighlightSeverity.WARNING &&
                    it.description?.contains("Conflicting flags", ignoreCase = true) == true
        }
        assertEquals(
            "Conflicting flags 'ignoreversion'/'comparetimestamp' must produce exactly 2 WARNING annotations",
            2, conflicts.size
        )
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

    // ── Constant: known builtin ───────────────────────────────────────────────

    fun testKnownBuiltinConstantHighlightedAsReference() {
        val text = VALID_SETUP + "\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val fileText = myFixture.run {
            configureByText(IssFileType.INSTANCE, text); file.text
        }
        val all = myFixture.doHighlighting()
        val appOffset = fileText.lastIndexOf("{app}")
        val hit = all.any {
            it.forcedTextAttributesKey == IsiAnnotatorHighlighting.REFERENCE &&
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

    // ── Preprocessor directive keyword ────────────────────────────────────────

    fun testPreprocessorIncludeKeywordHighlighted() {
        val text = "#include \"other.iss\"\n" + VALID_SETUP
        val fileText = myFixture.run {
            configureByText(IssFileType.INSTANCE, text); file.text
        }
        val all = myFixture.doHighlighting()
        val hit = all.any {
            it.forcedTextAttributesKey == IsiAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE &&
                    fileText.substring(it.startOffset, it.endOffset) == "#include"
        }
        assertTrue("'#include' directive must be highlighted with PREPROCESSOR_DIRECTIVE", hit)
    }

    // ── Reference type value ──────────────────────────────────────────────────

    fun testTypesReferenceValueHighlightedAsReference() {
        val text = VALID_SETUP +
                "\n[Types]\nName: full; Description: \"Full\"\n" +
                "\n[Components]\nName: core; Description: \"Core\"; Types: full\n"
        val fileText = myFixture.run {
            configureByText(IssFileType.INSTANCE, text); file.text
        }
        val all = myFixture.doHighlighting()
        val offset = fileText.lastIndexOf("full")
        val hit = all.any {
            it.forcedTextAttributesKey == IsiAnnotatorHighlighting.REFERENCE &&
                    it.startOffset == offset
        }
        assertTrue("'full' in Types: parameter must be highlighted with REFERENCE attribute", hit)
    }
}

package org.pcsoft.intellij.plugin.inno_setup.language.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IssCompletionTest : BasePlatformTestCase() {

    // ── Section name completion ───────────────────────────────────────────────

    fun testSectionNameCompletion() {
        myFixture.configureByText(IssFileType.INSTANCE, "[<caret>")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected section name suggestions", variants)
        assertTrue("Expected 'Setup' in section names", "Setup" in variants!!)
        assertTrue("Expected 'Files' in section names", "Files" in variants)
    }

    // ── Directive key completion ([Setup]-style Key=Value) ────────────────────

    fun testDirectiveKeyCompletionAtLineStart() {
        // Caret at the very beginning of an empty line inside [Setup]
        myFixture.configureByText(IssFileType.INSTANCE, "[Setup]\n<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected directive key suggestions on empty line in [Setup]", variants)
        assertTrue("Expected 'AppName' in directive key suggestions", "AppName" in variants!!)
        assertTrue("Expected 'AppVersion' in directive key suggestions", "AppVersion" in variants)
    }

    fun testDirectiveKeyCompletionMidWord() {
        // Caret in the middle of a partially-typed key that already has '=' on the line
        myFixture.configureByText(IssFileType.INSTANCE, "[Setup]\nApp<caret>=My Program\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected directive key suggestions mid-word", variants)
        assertTrue("Expected 'AppName' in suggestions", "AppName" in variants!!)
    }

    // ── Parameter key completion ([Files]-style Key: Value) ───────────────────

    fun testParamKeyCompletionAtLineStart() {
        // Caret at the beginning of an empty line inside [Files]
        myFixture.configureByText(IssFileType.INSTANCE, "[Files]\n<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected parameter key suggestions on empty line in [Files]", variants)
        assertTrue("Expected 'Source' in parameter key suggestions", "Source" in variants!!)
        assertTrue("Expected 'DestDir' in parameter key suggestions", "DestDir" in variants)
    }

    fun testParamKeyCompletionMidWord() {
        // Caret in the middle of a partially-typed key that already has ': ...' on the line
        myFixture.configureByText(IssFileType.INSTANCE, "[Files]\nSo<caret>: \"app.exe\"\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected parameter key suggestions mid-word", variants)
        assertTrue("Expected 'Source' in suggestions", "Source" in variants!!)
    }

    // ── ISPP variable completion ──────────────────────────────────────────────────

    fun testIsppVariableCompletionAfterHash() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n#define OutputDir \"out\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#<caret>}\"\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected ISPP variable suggestions after {#", variants)
        assertTrue("Expected 'AppVersion' in suggestions", "AppVersion" in variants!!)
        assertTrue("Expected 'OutputDir' in suggestions", "OutputDir" in variants)
    }

    fun testIsppVariableShownInBracePopup() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{<caret>\"\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected constant suggestions after {", variants)
        assertTrue("Expected '#AppVersion' in { popup", "#AppVersion" in variants!!)
    }

    // ── #define datatype completion ───────────────────────────────────────────

    fun testDefineTypeCompletionAfterDefine() {
        myFixture.configureByText(IssFileType.INSTANCE, "#define <caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected datatype suggestions after '#define '", variants)
        assertTrue("Expected 'int' after #define", "int" in variants!!)
        assertTrue("Expected 'str' after #define", "str" in variants)
        assertTrue("Expected 'float' after #define", "float" in variants)
    }

    fun testDefineTypeCompletionMidWord() {
        myFixture.configureByText(IssFileType.INSTANCE, "#define i<caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected datatype suggestions for '#define i'", variants)
        assertTrue("Expected 'int' for prefix 'i'", "int" in variants!!)
        assertTrue("Expected 'integer' for prefix 'i'", "integer" in variants)
        assertFalse("Did not expect 'str' for prefix 'i'", "str" in variants)
    }

    fun testNoDefineTypeCompletionAtNamePosition() {
        // After a complete first token + space the cursor is at the NAME slot — no type suggestions.
        myFixture.configureByText(IssFileType.INSTANCE, "#define int <caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertTrue(
            "Type keywords must not be suggested at the #define name position",
            variants == null || ("str" !in variants && "float" !in variants)
        )
    }
}

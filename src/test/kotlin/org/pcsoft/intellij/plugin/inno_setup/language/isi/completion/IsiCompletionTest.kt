package org.pcsoft.intellij.plugin.inno_setup.language.isi.completion

import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.JBColor
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IsiCompletionTest : BasePlatformTestCase() {

    /** Foreground color the completion popup would render for the given key, or null if absent. */
    private fun foregroundOf(name: String): java.awt.Color? =
        myFixture.lookupElements?.firstOrNull { element ->
            LookupElementPresentation().also { element.renderElement(it) }.itemText == name
        }?.let { element ->
            LookupElementPresentation().also { element.renderElement(it) }.itemTextForeground
        }

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

    fun testParamKeyCompletionAfterSemicolon() {
        // Caret after a "; " separator on a [Files] line — must still offer keys
        myFixture.configureByText(IssFileType.INSTANCE, "[Files]\nSource: \"app.exe\"; <caret>\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected parameter key suggestions after ';'", variants)
        assertTrue("Expected 'DestDir' in suggestions after ';'", "DestDir" in variants!!)
    }

    fun testParamKeyDuplicateIsPerLineNotPerSection() {
        // DestDir is used on the FIRST line; on the second line it must NOT be
        // flagged as a duplicate (red), because parameter keys are per-line.
        myFixture.configureByText(
            IssFileType.INSTANCE,
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

    // ── ISPP variable completion ──────────────────────────────────────────────────

    fun testIsppVariableCompletionAfterHash() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
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
            IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{<caret>\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected constant suggestions after {", variants)
        assertTrue("Expected '#AppVersion' in { popup", "#AppVersion" in variants!!)
    }

}

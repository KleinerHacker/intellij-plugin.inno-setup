package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.*

class IssParserTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/resources"

    fun testSimpleIssNoParseErrors() {
        myFixture.configureByFile("scripts/simple.iss")
        val errors = PsiTreeUtil.collectElementsOfType(myFixture.file, PsiErrorElement::class.java)
        assertTrue(
            "Expected no parse errors but found:\n" +
                errors.joinToString("\n") { "  '${it.errorDescription}' at offset ${it.textOffset}: '${it.text}'" },
            errors.isEmpty()
        )
    }

    fun testTwoDirectivesNoError() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Setup]\nAppName=My Program\nAppVersion=1.5\n")
        val errors = PsiTreeUtil.collectElementsOfType(myFixture.file, PsiErrorElement::class.java)
        assertTrue("Two consecutive directive entries should parse without errors", errors.isEmpty())
    }

    fun testSimpleIssSectionCount() {
        myFixture.configureByFile("scripts/simple.iss")
        val file = myFixture.file as? IssFile ?: error("Not an IssFile")
        val sections = file.sections()
        assertEquals("Expected 3 sections", 3, sections.size)
        assertEquals("Setup", sections[0].nameText())
        assertEquals("Files", sections[1].nameText())
        assertEquals("Icons", sections[2].nameText())
    }

    fun testSetupSectionDirectiveCount() {
        myFixture.configureByFile("scripts/simple.iss")
        val file = myFixture.file as? IssFile ?: error("Not an IssFile")
        val setup = file.findSection("Setup") ?: error("No [Setup] section")
        // AppName, AppVersion, WizardStyle, DefaultDirName, DefaultGroupName,
        // UninstallDisplayIcon, Compression  (2 commented-out lines not counted)
        assertEquals("Expected 7 directive entries", 7, setup.directiveEntryList.size)
    }

    fun testFilesSectionParameterCount() {
        myFixture.configureByFile("scripts/simple.iss")
        val file = myFixture.file as? IssFile ?: error("Not an IssFile")
        val files = file.findSection("Files") ?: error("No [Files] section")
        assertEquals("Expected 3 parameter entries", 3, files.parameterEntryList.size)
    }

    fun testLastLineWithoutNewlineNoError() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Setup]\nAppName=My Program")
        val errors = PsiTreeUtil.collectElementsOfType(myFixture.file, PsiErrorElement::class.java)
        assertTrue("Last line without trailing newline should not cause parse errors", errors.isEmpty())
    }

    fun testSlashSlashCommentRecognized() {
        myFixture.configureByText(IssFileType.INSTANCE, "// comment\n[Setup]\nAppName=Test\n")
        val errors = PsiTreeUtil.collectElementsOfType(myFixture.file, PsiErrorElement::class.java)
        assertTrue("// comment should be recognized without parse errors", errors.isEmpty())
    }
}

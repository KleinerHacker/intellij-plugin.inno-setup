package org.pcsoft.intellij.plugin.inno_setup.language.isi

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IsiParserTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/resources"

    /** Returns the top-level IssFile regardless of whether myFixture.file is injected. */
    private fun issFile(): IssFile {
        val rawFile = myFixture.file
        if (rawFile is IssFile) return rawFile
        return InjectedLanguageManager.getInstance(myFixture.project)
            .getTopLevelFile(rawFile) as? IssFile
            ?: error("Expected IssFile but got ${rawFile.javaClass.name}")
    }

    fun testSimpleIssNoParseErrors() {
        myFixture.configureByFile("scripts/simple.iss")
        val file = issFile()
        val errors = PsiTreeUtil.collectElementsOfType(file, PsiErrorElement::class.java)
        assertTrue(
            "Expected no parse errors but found:\n" +
                    errors.joinToString("\n") { "  '${it.errorDescription}' at offset ${it.textOffset}: '${it.text}'" },
            errors.isEmpty()
        )
    }

    fun testTwoDirectivesNoError() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Setup]\nAppName=My Program\nAppVersion=1.5\n")
        val errors = PsiTreeUtil.collectElementsOfType(issFile(), PsiErrorElement::class.java)
        assertTrue("Two consecutive directive entries should parse without errors", errors.isEmpty())
    }

    fun testSimpleIssSectionCount() {
        myFixture.configureByFile("scripts/simple.iss")
        val file = issFile()
        val sections = file.sections()
        assertEquals("Expected 3 sections", 3, sections.size)
        assertEquals("Setup", sections[0].nameText())
        assertEquals("Files", sections[1].nameText())
        assertEquals("Icons", sections[2].nameText())
    }

    fun testSetupSectionDirectiveCount() {
        myFixture.configureByFile("scripts/simple.iss")
        val setup = issFile().findSection("Setup") ?: error("No [Setup] section")
        // AppName, AppVersion, WizardStyle, DefaultDirName, DefaultGroupName,
        // UninstallDisplayIcon, Compression  (2 commented-out lines not counted)
        assertEquals("Expected 7 directive entries", 7, setup.directiveEntryList.size)
    }

    fun testFilesSectionParameterCount() {
        myFixture.configureByFile("scripts/simple.iss")
        val files = issFile().findSection("Files") ?: error("No [Files] section")
        assertEquals("Expected 3 parameter entries", 3, files.parameterEntryList.size)
    }

    fun testLastLineWithoutNewlineNoError() {
        myFixture.configureByText(IssFileType.INSTANCE, "[Setup]\nAppName=My Program")
        val errors = PsiTreeUtil.collectElementsOfType(issFile(), PsiErrorElement::class.java)
        assertTrue("Last line without trailing newline should not cause parse errors", errors.isEmpty())
    }

    fun testSlashSlashCommentRecognized() {
        myFixture.configureByText(IssFileType.INSTANCE, "// comment\n[Setup]\nAppName=Test\n")
        val errors = PsiTreeUtil.collectElementsOfType(issFile(), PsiErrorElement::class.java)
        assertTrue("// comment should be recognized without parse errors", errors.isEmpty())
    }

    fun testSimpleIssPsiTree() {
        myFixture.configureByFile("scripts/simple.iss")
        val actualTree = DebugUtil.psiToString(issFile(), false).trimEnd()
        assertSameLinesWithFile("$testDataPath/scripts/simple.tree", actualTree)
    }
}

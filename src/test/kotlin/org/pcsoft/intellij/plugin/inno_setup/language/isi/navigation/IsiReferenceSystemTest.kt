package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiIsppLine
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx

/**
 * End-to-end tests for both reference types:
 *   1. ISPP constant references  {#Name} → #define Name  (now in injected ISPP PSI)
 *   2. Section cross-references  Tasks: name → [Tasks] Name: name
 */
class IsiReferenceSystemTest : BasePlatformTestCase() {

    private fun ref(content: String): PsiReference? {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        return issFile().findReferenceAt(myFixture.caretOffset)
    }

    private fun issFile(): IssFile {
        val rawFile = myFixture.file
        if (rawFile is IssFile) return rawFile
        return InjectedLanguageManager.getInstance(myFixture.project)
            .getTopLevelFile(rawFile) as IssFile
    }

    private fun findDefine(name: String): IsppDirective? {
        val file = issFile()
        val mgr = InjectedLanguageManager.getInstance(file.project)
        return PsiTreeUtil.getChildrenOfTypeAsList(file, IsiIsppLine::class.java)
            .flatMap { line ->
                val dirs = mutableListOf<IsppDirective>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsppFile)
                        dirs.addAll(PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsppDirective::class.java))
                }
                dirs
            }
            .firstOrNull { (it as? IsppDirectiveEx)?.getDefineName() == name }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ISPP References  {#Name} → #define Name
    // ═══════════════════════════════════════════════════════════════════════════

    fun testIsppRefFoundAtCaretOnIdentifier() {
        val r = ref("#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Version}\"\n")
        assertNotNull("findReferenceAt should return a reference when caret is on the IDENTIFIER in {#Name}", r)
    }

    fun testIsppRefFoundInBareValue() {
        val r = ref("#define MyDir \"C:\\\\app\"\n[Setup]\nAppName=Test\nOutputDir={#My<caret>Dir}\n")
        assertNotNull("findReferenceAt should return a reference for {#Name} in a bare directive value", r)
    }

    fun testIsppRefNotFoundOnHashToken() {
        val r = ref("#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{<caret>#AppVersion}\"\n")
        assertNull("findReferenceAt should return null when caret is on '{' (not the identifier)", r)
    }

    fun testIsppRefResolvesToDefineDirective() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Version}\"\n"
        )
        val resolved = issFile().findReferenceAt(myFixture.caretOffset)?.resolve()
        assertNotNull("Reference should resolve to the #define directive", resolved)
        assertInstanceOf(resolved, IsppDirective::class.java)
        assertEquals("AppVersion", (resolved as IsppDirectiveEx).getDefineName())
    }

    fun testIsppRefUnknownResolvesToNull() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "[Files]\nSource: \"a.exe\"; DestDir: \"{#Unkno<caret>wn}\"\n"
        )
        val ref = issFile().findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference object should still exist for an unknown ISPP name", ref)
        assertNull("Unknown ISPP constant should resolve to null", ref!!.resolve())
    }

    fun testIsppRefIsReferenceToDirective() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define MyVar \"value\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyV<caret>ar}\"\n"
        )
        val ref = issFile().findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference must exist at caret", ref)
        val directive = findDefine("MyVar")
        assertNotNull("Expected #define MyVar in the file", directive)
        assertTrue("isReferenceTo(directive) must be true", ref!!.isReferenceTo(directive!!))
    }

    fun testIsppRefIsReferenceToNameIdentifier() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define MyVar \"value\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyV<caret>ar}\"\n"
        )
        val ref = issFile().findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference must exist at caret", ref)
        val directive = findDefine("MyVar")
        val nameId = (directive as? IsppDirectiveEx)?.getNameIdentifier()
        assertNotNull("Expected nameIdentifier on #define MyVar", nameId)
        assertTrue("isReferenceTo(nameIdentifier) must be true for Highlight Usages", ref!!.isReferenceTo(nameId!!))
    }

    fun testIsppRefIsReferenceToWrongDirective() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define MyVar \"v\"\n#define Other \"x\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyV<caret>ar}\"\n"
        )
        val ref = issFile().findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference must exist at caret", ref)
        val other = findDefine("Other")
        assertNotNull("Expected #define Other in the file", other)
        assertFalse("isReferenceTo(Other) must be false", ref!!.isReferenceTo(other!!))
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Section cross-references  Tasks: name → [Tasks] Name: name
    // ═══════════════════════════════════════════════════════════════════════════

    private val SECTION_REF_SCRIPT = """
        [Setup]
        AppName=TestApp
        AppVersion=1.0

        [Tasks]
        Name: maintask; Description: "Main Task"
        Name: othertask; Description: "Other Task"

        [Files]
        Source: "app.exe"; DestDir: "{app}"; Tasks: main<caret>task

    """.trimIndent()

    fun testSectionRefFoundAtCaret() {
        val r = ref(SECTION_REF_SCRIPT)
        assertNotNull("findReferenceAt should return a reference for Tasks: <name>", r)
    }

    fun testSectionRefResolvesToNamePairValue() {
        myFixture.configureByText(IssFileType.INSTANCE, SECTION_REF_SCRIPT)
        val resolved = issFile().findReferenceAt(myFixture.caretOffset)?.resolve()
        assertNotNull("Tasks: maintask should resolve to the Name paramValue in [Tasks]", resolved)
    }

    fun testSectionRefResolvesCorrectEntry() {
        myFixture.configureByText(IssFileType.INSTANCE, SECTION_REF_SCRIPT)
        val resolved = issFile().findReferenceAt(myFixture.caretOffset)?.resolve()
        assertNotNull(resolved)
        assertTrue(
            "Resolved element text should contain 'maintask'",
            resolved!!.text.contains("maintask", ignoreCase = true)
        )
    }

    fun testSectionRefUnknownResolvesToNull() {
        myFixture.configureByText(
            IssFileType.INSTANCE, """
            [Setup]
            AppName=TestApp
            AppVersion=1.0

            [Tasks]
            Name: maintask; Description: "Main Task"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Tasks: unkno<caret>wn

        """.trimIndent()
        )
        val ref = issFile().findReferenceAt(myFixture.caretOffset)
        assertNotNull("A reference object should exist even for unknown name", ref)
        assertNull("Unknown Tasks name should resolve to null", ref!!.resolve())
    }

    fun testSectionRefNotCreatedForNonRefKey() {
        val r = ref(
            """
            [Files]
            Source: "app<caret>.exe"; DestDir: "{app}"

        """.trimIndent()
        )
        assertNull("Source: value should not produce a cross-section reference", r)
    }

    fun testSectionRefComponentsKey() {
        val r = ref(
            """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Components]
            Name: maincomp; Description: "Main"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Components: main<caret>comp

        """.trimIndent()
        )
        assertNotNull("findReferenceAt should return a reference for Components: <name>", r)
    }

    fun testSectionRefIsReferenceToNamePair() {
        myFixture.configureByText(IssFileType.INSTANCE, SECTION_REF_SCRIPT)
        val ref = issFile().findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference must exist at caret", ref)
        val resolved = ref!!.resolve()
        assertNotNull("Reference must resolve", resolved)
        val namePair = resolved?.parent as? IsiParamPair
        assertNotNull("Resolved value's parent should be a paramPair", namePair)
        assertTrue("isReferenceTo(resolved) must be true", ref.isReferenceTo(resolved!!))
    }
}

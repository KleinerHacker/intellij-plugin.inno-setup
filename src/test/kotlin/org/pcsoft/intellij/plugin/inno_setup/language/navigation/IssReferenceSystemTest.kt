package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirectiveEx

/**
 * End-to-end tests for both reference types:
 *   1. ISPP constant references  {#Name} → #define Name
 *   2. Section cross-references  Tasks: name → [Tasks] Name: name
 *
 * These tests use myFixture.file.findReferenceAt() which is what IntelliJ calls
 * internally for Ctrl+B (Go to Declaration) — they catch wiring bugs that
 * unit tests on reference classes alone cannot detect.
 */
class IssReferenceSystemTest : BasePlatformTestCase() {

    private fun ref(content: String): PsiReference? {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        return myFixture.file.findReferenceAt(myFixture.caretOffset)
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
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Version}\"\n")
        val resolved = myFixture.file.findReferenceAt(myFixture.caretOffset)?.resolve()
        assertNotNull("Reference should resolve to the #define directive", resolved)
        assertInstanceOf(resolved, IssPreprocessorDirective::class.java)
        assertEquals("AppVersion", (resolved as IssPreprocessorDirectiveEx).getDefineName())
    }

    fun testIsppRefUnknownResolvesToNull() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "[Files]\nSource: \"a.exe\"; DestDir: \"{#Unkno<caret>wn}\"\n")
        val ref = myFixture.file.findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference object should still exist for an unknown ISPP name", ref)
        assertNull("Unknown ISPP constant should resolve to null", ref!!.resolve())
    }

    fun testIsppRefIsReferenceToDirective() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define MyVar \"value\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyV<caret>ar}\"\n")
        val ref = myFixture.file.findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference must exist at caret", ref)

        val directive = PsiTreeUtil.findChildrenOfType(myFixture.file, IssPreprocessorDirective::class.java)
            .firstOrNull { (it as? IssPreprocessorDirectiveEx)?.getDefineName() == "MyVar" }
        assertNotNull("Expected #define MyVar in the file", directive)

        assertTrue("isReferenceTo(directive) must be true", ref!!.isReferenceTo(directive!!))
    }

    fun testIsppRefIsReferenceToNameIdentifier() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define MyVar \"value\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyV<caret>ar}\"\n")
        val ref = myFixture.file.findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference must exist at caret", ref)

        val directive = PsiTreeUtil.findChildrenOfType(myFixture.file, IssPreprocessorDirective::class.java)
            .firstOrNull { (it as? IssPreprocessorDirectiveEx)?.getDefineName() == "MyVar" }
        val nameId = (directive as? IssPreprocessorDirectiveEx)?.getNameIdentifier()
        assertNotNull("Expected nameIdentifier on #define MyVar", nameId)

        assertTrue("isReferenceTo(nameIdentifier) must be true for Highlight Usages", ref!!.isReferenceTo(nameId!!))
    }

    fun testIsppRefIsReferenceToWrongDirective() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define MyVar \"v\"\n#define Other \"x\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyV<caret>ar}\"\n")
        val ref = myFixture.file.findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference must exist at caret", ref)

        val other = PsiTreeUtil.findChildrenOfType(myFixture.file, IssPreprocessorDirective::class.java)
            .firstOrNull { (it as? IssPreprocessorDirectiveEx)?.getDefineName() == "Other" }
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
        val resolved = myFixture.file.findReferenceAt(myFixture.caretOffset)?.resolve()
        assertNotNull("Tasks: maintask should resolve to the Name paramValue in [Tasks]", resolved)
    }

    fun testSectionRefResolvesCorrectEntry() {
        myFixture.configureByText(IssFileType.INSTANCE, SECTION_REF_SCRIPT)
        val resolved = myFixture.file.findReferenceAt(myFixture.caretOffset)?.resolve()
        assertNotNull(resolved)
        assertTrue("Resolved element text should contain 'maintask'",
            resolved!!.text.contains("maintask", ignoreCase = true))
    }

    fun testSectionRefUnknownResolvesToNull() {
        myFixture.configureByText(IssFileType.INSTANCE, """
            [Setup]
            AppName=TestApp
            AppVersion=1.0

            [Tasks]
            Name: maintask; Description: "Main Task"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Tasks: unkno<caret>wn

        """.trimIndent())
        val ref = myFixture.file.findReferenceAt(myFixture.caretOffset)
        assertNotNull("A reference object should exist even for unknown name", ref)
        assertNull("Unknown Tasks name should resolve to null", ref!!.resolve())
    }

    fun testSectionRefNotCreatedForNonRefKey() {
        val r = ref("""
            [Files]
            Source: "app<caret>.exe"; DestDir: "{app}"

        """.trimIndent())
        assertNull("Source: value should not produce a cross-section reference", r)
    }

    fun testSectionRefComponentsKey() {
        val r = ref("""
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Components]
            Name: maincomp; Description: "Main"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Components: main<caret>comp

        """.trimIndent())
        assertNotNull("findReferenceAt should return a reference for Components: <name>", r)
    }

    fun testSectionRefIsReferenceToNamePair() {
        myFixture.configureByText(IssFileType.INSTANCE, SECTION_REF_SCRIPT)
        val ref = myFixture.file.findReferenceAt(myFixture.caretOffset)
        assertNotNull("Reference must exist at caret", ref)

        val resolved = ref!!.resolve()
        assertNotNull("Reference must resolve", resolved)

        val namePair = resolved?.parent as? IssParamPair
        assertNotNull("Resolved value's parent should be a paramPair", namePair)

        assertTrue("isReferenceTo(resolved) must be true", ref.isReferenceTo(resolved!!))
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Diagnostics — reveal WHERE the wiring breaks
    // ═══════════════════════════════════════════════════════════════════════════

    fun testDiagnostic_isppLeafType() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Version}\"\n")
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Leaf element at caret must exist", leaf)
        assertEquals("Leaf element type should be IDENTIFIER",
            "IDENTIFIER", leaf!!.node.elementType.toString())
        assertEquals("Leaf text should be AppVersion", "AppVersion", leaf.text)
    }

    fun testDiagnostic_isppLeafParentType() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Version}\"\n")
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)!!
        val parent = leaf.parent
        assertNotNull("IDENTIFIER must have a parent", parent)
        assertTrue("IDENTIFIER parent should be IssConstantBody",
            parent is org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssConstantBody)
    }

    fun testDiagnostic_isppLeafDirectReferences() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Version}\"\n")
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)!!
        val body = leaf.parent  // IssConstantBody — references live here via mixin
        assertNotNull("IDENTIFIER must have an IssConstantBody parent", body)
        val refs = body!!.references
        assertTrue("IssConstantBody should have at least one reference via mixin " +
                "(got ${refs.size}; types: ${refs.map { it.javaClass.simpleName }})",
            refs.isNotEmpty())
    }

    fun testDiagnostic_isppConstantBodyReferences() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Version}\"\n")
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)!!
        val body = leaf.parent
        assertNotNull(body)
        val refs = body!!.references
        assertTrue("IssConstantBody should have at least one reference from contributor " +
                "(got ${refs.size}; types: ${refs.map { it.javaClass.simpleName }})",
            refs.isNotEmpty())
    }

    fun testDiagnostic_sectionRefLeafType() {
        myFixture.configureByText(IssFileType.INSTANCE, SECTION_REF_SCRIPT)
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Leaf element at caret must exist", leaf)
        assertEquals("Leaf element type should be IDENTIFIER",
            "IDENTIFIER", leaf!!.node.elementType.toString())
        assertEquals("Leaf text should be maintask", "maintask", leaf.text)
    }

    fun testDiagnostic_sectionRefParamValueReferences() {
        myFixture.configureByText(IssFileType.INSTANCE, SECTION_REF_SCRIPT)
        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)!!
        val paramValue = PsiTreeUtil.getParentOfType(leaf,
            org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamValue::class.java)
        assertNotNull("Leaf must have IssParamValue ancestor", paramValue)
        val refs = paramValue!!.references
        assertTrue("IssParamValue should have at least one reference from contributor " +
                "(got ${refs.size}; types: ${refs.map { it.javaClass.simpleName }})",
            refs.isNotEmpty())
    }
}

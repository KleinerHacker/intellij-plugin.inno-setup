package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssIsppLine

class IssIsppReferenceTest : BasePlatformTestCase() {

    private fun setup(content: String): IssFile {
        val file = myFixture.configureByText(IssFileType.INSTANCE, content)
        if (file is IssFile) return file
        // In IntelliJ 2025.3, configureByText may return the injected IsppFile when
        // the content starts with a preprocessor line. Use getTopLevelFile to recover.
        return InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IssFile
    }

    private fun findIsppConstantBody(file: IssFile): IssConstantBody? =
        PsiTreeUtil.findChildrenOfType(file, IssConstantBody::class.java)
            .firstOrNull { it.text.startsWith("#") }

    private fun findDefine(file: IssFile, name: String): IsppDirective? {
        val mgr = InjectedLanguageManager.getInstance(file.project)
        return PsiTreeUtil.getChildrenOfTypeAsList(file, IssIsppLine::class.java)
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

    // ── resolve ──────────────────────────────────────────────────────────────

    fun testIsppConstantResolvesToDefine() {
        val file = setup("#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n")
        val body = findIsppConstantBody(file)
        assertNotNull("Expected IssConstantBody for {#AppVersion}", body)

        val ref = IssIsppConstantReference(body!!, "AppVersion")
        val resolved = ref.resolve()

        assertNotNull("Expected {#AppVersion} to resolve to #define directive", resolved)
        assertInstanceOf(resolved, IsppDirective::class.java)
        assertEquals("AppVersion", (resolved as IsppDirectiveEx).getDefineName())
    }

    fun testUnknownIsppConstantDoesNotResolve() {
        val file = setup("[Files]\nSource: \"app.exe\"; DestDir: \"{#Unknown}\"\n")
        val body = findIsppConstantBody(file)
        assertNotNull("Expected IssConstantBody for {#Unknown}", body)

        val ref = IssIsppConstantReference(body!!, "Unknown")
        assertNull("Unknown ISPP constant should not resolve", ref.resolve())
    }

    // ── isReferenceTo ────────────────────────────────────────────────────────

    fun testIsReferenceToDirective() {
        val file = setup("#define MyVar \"value\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#MyVar}\"\n")
        val body = findIsppConstantBody(file)!!
        val directive = findDefine(file, "MyVar")
        assertNotNull("Expected #define MyVar directive", directive)

        val ref = IssIsppConstantReference(body, "MyVar")
        assertTrue("isReferenceTo should return true for the directive element",
            ref.isReferenceTo(directive!!))
    }

    fun testIsReferenceToNameIdentifier() {
        val file = setup("#define MyVar \"value\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#MyVar}\"\n")
        val body = findIsppConstantBody(file)!!
        val directive = findDefine(file, "MyVar")
        val nameId = (directive as? IsppDirectiveEx)?.getNameIdentifier()
        assertNotNull("Expected name identifier on #define MyVar", nameId)

        val ref = IssIsppConstantReference(body, "MyVar")
        assertTrue("isReferenceTo should return true when IntelliJ passes the name identifier",
            ref.isReferenceTo(nameId!!))
    }

    fun testIsReferenceToWrongNameReturnsFalse() {
        val file = setup("#define MyVar \"value\"\n#define Other \"x\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#MyVar}\"\n")
        val body = findIsppConstantBody(file)!!
        val otherDirective = findDefine(file, "Other")
        assertNotNull("Expected #define Other directive", otherDirective)

        val ref = IssIsppConstantReference(body, "MyVar")
        assertFalse("isReferenceTo should return false for a different directive",
            ref.isReferenceTo(otherDirective!!))
    }

    // ── define name extraction ───────────────────────────────────────────────

    fun testDefineNameExtracted() {
        val file = setup("#define MyConst \"hello\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        val directive = findDefine(file, "MyConst")
        assertNotNull("Expected #define directive", directive)
        assertEquals("MyConst", (directive as IsppDirectiveEx).getDefineName())
    }

    // ── rename ───────────────────────────────────────────────────────────────

    fun testRenameDefineUpdatesConstantReference() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define App<caret>Version \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        )
        myFixture.renameElementAtCaret("NewVersion")
        myFixture.checkResult(
            "#define NewVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#NewVersion}\"\n"
        )
    }

    fun testRenameDefineUpdatesMultipleReferences() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define App<caret>Name \"MyApp\"\n[Setup]\nAppName={#AppName}\nAppPublisher={#AppName}\n"
        )
        myFixture.renameElementAtCaret("ProductName")
        myFixture.checkResult(
            "#define ProductName \"MyApp\"\n[Setup]\nAppName={#ProductName}\nAppPublisher={#ProductName}\n"
        )
    }
}

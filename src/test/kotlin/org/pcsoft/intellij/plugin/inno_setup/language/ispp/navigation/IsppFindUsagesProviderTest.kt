package org.pcsoft.intellij.plugin.inno_setup.language.ispp.navigation

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiIsppLine
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx

/**
 * Tests for [IsppFindUsagesProvider] — find-usages is only enabled for `#define`
 * directives, and the descriptive name/type strings come from the define.
 */
class IsppFindUsagesProviderTest : BasePlatformTestCase() {

    private val provider = IsppFindUsagesProvider()

    private fun setup(content: String): IssFile {
        val file = myFixture.configureByText(IssFileType.INSTANCE, content)
        return if (file is IssFile) file
        else InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IssFile
    }

    /** All ISPP directives across injected preprocessor lines. */
    private fun directives(file: IssFile): List<IsppDirective> {
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
    }

    fun testCanFindUsagesForDefine() {
        val file = setup("#define MyVar \"x\"\n#include \"other.iss\"\n")
        val define = directives(file).first { (it as IsppDirectiveEx).isDefine() }
        assertTrue("find-usages must be enabled for #define", provider.canFindUsagesFor(define))
    }

    fun testCannotFindUsagesForNonDefineDirective() {
        val file = setup("#define MyVar \"x\"\n#include \"other.iss\"\n")
        val include = directives(file).first { !(it as IsppDirectiveEx).isDefine() }
        assertFalse("find-usages must be disabled for #include", provider.canFindUsagesFor(include))
    }

    fun testCannotFindUsagesForUnrelatedElement() {
        val file = setup("#define MyVar \"x\"\n")
        assertFalse(
            "find-usages must be disabled for a non-directive element",
            provider.canFindUsagesFor(file)
        )
    }

    fun testDescriptiveNameAndType() {
        val file = setup("#define MyVar \"x\"\n")
        val define = directives(file).first { (it as IsppDirectiveEx).isDefine() }
        assertEquals("ISPP define", provider.getType(define))
        assertEquals("MyVar", provider.getDescriptiveName(define))
        assertEquals(provider.getDescriptiveName(define), provider.getNodeText(define, true))
    }

    fun testHelpIdIsNullAndWordsScannerExists() {
        val file = setup("#define MyVar \"x\"\n")
        val define = directives(file).first { (it as IsppDirectiveEx).isDefine() }
        assertNull(provider.getHelpId(define))
        assertNotNull(provider.getWordsScanner())
    }
}

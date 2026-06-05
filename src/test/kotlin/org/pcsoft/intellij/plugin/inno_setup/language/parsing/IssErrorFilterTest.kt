package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Tests for [IssErrorFilter] — parse errors inside the `[Code]` section or an
 * ISPP preprocessor line must be suppressed, all others highlighted.
 *
 * An unclosed section header (`[Name` without `]`) pins after `[` and yields a
 * [PsiErrorElement] inside that section, giving a deterministic in-section error.
 */
class IssErrorFilterTest : BasePlatformTestCase() {

    private val filter = IssErrorFilter()

    private fun issFile(content: String): IssFile {
        val file = myFixture.configureByText(IssFileType.INSTANCE, content)
        return if (file is IssFile) file
        else InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IssFile
    }

    private fun errorsIn(content: String): List<PsiErrorElement> =
        PsiTreeUtil.collectElementsOfType(issFile(content), PsiErrorElement::class.java).toList()

    fun testErrorInCodeSectionIsSuppressed() {
        // Unclosed [Code header → error inside the [Code] section; no body lines so
        // no additional errors leak out to the file level.
        val errors = errorsIn("[Setup]\nAppName=x\nAppVersion=1\n\n[Code\n")
        assertTrue("Expected a parse error from the unclosed [Code header", errors.isNotEmpty())
        assertTrue("Errors inside the [Code] section must be suppressed",
            errors.all { !filter.shouldHighlightErrorElement(it) })
    }

    fun testErrorInNormalSectionIsHighlighted() {
        val errors = errorsIn("[Setup]\nAppName=x\nAppVersion=1\n\n[Files\nSource: \"a.exe\"\n")
        assertTrue("Expected a parse error from the unclosed [Files header", errors.isNotEmpty())
        assertTrue("Errors in a normal section must be highlighted",
            errors.all { filter.shouldHighlightErrorElement(it) })
    }
}

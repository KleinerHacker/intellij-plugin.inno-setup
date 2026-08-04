/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * A preprocessor line of a script may be continued on the following line with a trailing backslash. The host
 * lexer must keep such a continued line in a single opaque `HASH_LINE` token so that ISPP is injected over
 * all of its physical lines and the directive is parsed as one directive.
 */
class IsSectionPreprocessorContinuationTest : IsTimedBasePlatformTestCase() {

    /** Configures [text] as an `.iss` script and returns its top-level file. */
    private fun configure(text: String): PsiFile {
        myFixture.configureByText(IsScriptFileType.INSTANCE, text)
        return InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file)
    }

    /** The preprocessor lines of the configured script. */
    private fun preprocessorLines(text: String): List<IsSectionPreprocessorLine> =
        PsiTreeUtil.collectElementsOfType(configure(text), IsSectionPreprocessorLine::class.java).toList()

    /** The ISPP fragments injected into the first preprocessor line of [text]. */
    private fun injectedFiles(text: String): List<IsPreprocessorFile> {
        val line = preprocessorLines(text).first()
        val injected = mutableListOf<IsPreprocessorFile>()
        InjectedLanguageManager.getInstance(project).enumerate(line) { file, _ ->
            if (file is IsPreprocessorFile) injected += file
        }
        return injected
    }

    /** The single ISPP directive of the injected fragment of [text]. */
    private fun directiveOf(text: String): IsPreprocessorDirectiveEx {
        val injected = injectedFiles(text).single()
        val directives = PsiTreeUtil.collectElementsOfType(injected, IsPreprocessorDirective::class.java)
        assertEquals("A continued directive must stay one directive element", 1, directives.size)
        return directives.first() as IsPreprocessorDirectiveEx
    }

    /**
     * A `#define` continued with a backslash must produce exactly one preprocessor line whose `HASH_LINE`
     * token covers both physical lines.
     */
    fun testContinuedDirectiveIsOneHashLine() {
        val lines = preprocessorLines("#define MyVar \\\n    1\n[Setup]\n")
        assertEquals("The continued directive must stay one preprocessor line", 1, lines.size)
        val hashText = lines.first().node.findChildByType(IsSectionTypes.HASH_LINE)?.text
        assertEquals("#define MyVar \\\n    1", hashText)
    }

    /**
     * The section following a continued preprocessor line must still be recognised, i.e. the continuation
     * must not swallow the rest of the file, and the script must parse without errors.
     */
    fun testSectionAfterContinuedDirectiveIsParsed() {
        val file = configure("#define MyVar \\\n    1\n[Setup]\nAppName=Test\n")
        val lines = PsiTreeUtil.collectElementsOfType(file, IsSectionPreprocessorLine::class.java)
        assertEquals(1, lines.size)
        val errors = PsiTreeUtil.collectElementsOfType(file, PsiErrorElement::class.java)
        assertTrue(
            "A continued preprocessor line must not break the following section:\n" +
                    errors.joinToString("\n") { "  '${it.errorDescription}' at offset ${it.textOffset}" },
            errors.isEmpty()
        )
    }

    /**
     * ISPP must be injected over the whole continued directive, so the injected fragment contains the text
     * of both physical lines.
     */
    fun testInjectionCoversAllContinuedLines() {
        val injected = injectedFiles("#define MyVar \\\n    1\n")
        assertEquals("ISPP must be injected into the continued line", 1, injected.size)
        assertEquals("#define MyVar \\\n    1", injected.first().text)
    }

    /**
     * A `#define` whose value is spread over two physical lines must stay one directive that keeps its name.
     */
    fun testDefineWithContinuationIsOneDirective() {
        val directive = directiveOf("#define MyVar \\\n    1\n")
        assertTrue("The continued directive must still be a #define", directive.isDefine())
        assertEquals("MyVar", directive.getDefineName())
    }

    /**
     * Trailing spaces or tabs behind the backslash are tolerated: as long as nothing but whitespace follows it
     * up to the line break, the backslash still continues the directive on the next physical line.
     */
    fun testBackslashFollowedByWhitespaceIsStillAContinuation() {
        val injected = injectedFiles("#define MyVar \\  \n    1\n").first()
        assertEquals("#define MyVar \\  \n    1", injected.text)
    }

    /**
     * A function-like macro whose parameter list is broken across lines must keep all of its parameters.
     */
    fun testMacroParametersAcrossContinuedLines() {
        val directive = directiveOf("#define Sum(a, \\\n         b) a + b\n")
        assertTrue("The continued directive must be a function macro", directive.isFunctionMacro())
        assertEquals(listOf("a", "b"), directive.getMacroParameters())
    }

    /**
     * An `#if` condition spread over several lines must be reported as one condition expression.
     */
    fun testConditionAcrossContinuedLines() {
        val directive = directiveOf("#if Defined(A) && \\\n    Defined(B)\n#endif\n")
        assertTrue("The continued directive must be an #if", directive.isIf())
        assertNotNull("The condition must be detected across the line break", directive.getConditionExpressionText())
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine

/**
 * Tests for `#ifexist`/`#ifnexist`: the filename is a full ISPP **string** expression (type-checked,
 * references to `#define`s resolved) and the prepared on-disk resolution ([IsPreprocessorDirectiveEx.resolveExistFile]).
 * The existence *diagnostic* itself is a follow-up and is intentionally not asserted here.
 */
class IsPreprocessorIfExistTest : BasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun setup(content: String): IsScriptFile {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return if (file is IsScriptFile) file
        else InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IsScriptFile
    }

    private fun directives(file: IsScriptFile): List<IsPreprocessorDirective> {
        val mgr = InjectedLanguageManager.getInstance(file.project)
        return PsiTreeUtil.getChildrenOfTypeAsList(file, IsSectionPreprocessorLine::class.java)
            .flatMap { line ->
                val dirs = mutableListOf<IsPreprocessorDirective>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsPreprocessorFile)
                        dirs.addAll(PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsPreprocessorDirective::class.java))
                }
                dirs
            }
    }

    private fun ifExistDirective(file: IsScriptFile) =
        directives(file).first { (it as IsPreprocessorDirectiveEx).isIfExistFamily() } as IsPreprocessorDirectiveEx

    private fun errorsContaining(text: String, needle: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" && it.description?.contains(needle) == true }

    // ── string-type validation ─────────────────────────────────────────────────

    fun testLiteralFilenameIsValid() {
        val errors = myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, "#ifexist \"f.iss\"\n#endif\n$setupTail") }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" }
        assertTrue("A literal filename must not error, was: ${errors.map { it.description }}", errors.isEmpty())
    }

    fun testNonStringFilenameIsError() {
        val errors = errorsContaining("#ifexist 1+1\n#endif\n$setupTail", "requires a string value")
        assertTrue("A non-string #ifexist argument must be an error", errors.isNotEmpty())
    }

    fun testEmptyFilenameIsError() {
        val errors = errorsContaining("#ifexist\n#endif\n$setupTail", "requires a string value")
        assertTrue("#ifexist without a filename must be an error", errors.isNotEmpty())
    }

    // ── references inside the filename expression ───────────────────────────────

    fun testReferenceToStringDefineResolves() {
        val errors = errorsContaining(
            "#define P \"p.txt\"\n#ifexist P\n#endif\n$setupTail",
            "Unresolved preprocessor reference",
        )
        assertTrue("A reference to an existing #define in #ifexist must not error", errors.isEmpty())
    }

    fun testUnresolvedReferenceInIfExistIsError() {
        val errors = errorsContaining("#ifexist Missing\n#endif\n$setupTail", "Unresolved preprocessor reference")
        assertTrue("An unknown identifier in #ifexist must be an error", errors.isNotEmpty())
    }

    // ── prepared on-disk resolution (no diagnostic yet) ─────────────────────────

    fun testResolveExistFileFindsExistingFile() {
        myFixture.addFileToProject("target.iss", "; content\n")
        val directive = ifExistDirective(setup("#ifexist \"target.iss\"\n#endif\n$setupTail"))
        val resolved = directive.resolveExistFile()
        assertNotNull("An existing file must resolve", resolved)
        assertEquals("target.iss", resolved?.name)
    }

    fun testResolveExistFileMissingIsNull() {
        val directive = ifExistDirective(setup("#ifexist \"nope.iss\"\n#endif\n$setupTail"))
        assertNull("A missing file must resolve to null", directive.resolveExistFile())
    }
}

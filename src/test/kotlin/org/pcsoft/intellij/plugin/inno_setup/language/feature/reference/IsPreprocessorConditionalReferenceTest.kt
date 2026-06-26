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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine

/**
 * Tests that identifiers in a `#if`/`#elif` condition reference the matching `#define` (navigation, rename
 * and Find Usages), reusing [IsPreprocessorExpressionReference] like the `#define` expression path.
 */
class IsPreprocessorConditionalReferenceTest : IsTimedBasePlatformTestCase() {

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

    private fun ifDirective(file: IsScriptFile) =
        directives(file).first { (it as IsPreprocessorDirectiveEx).isIf() }

    private fun define(file: IsScriptFile, name: String) =
        directives(file).first { (it as IsPreprocessorDirectiveEx).getDefineName() == name }

    fun testIfReferenceResolvesToPrecedingDefine() {
        val file = setup("#define Foo 1\n#if Foo\n#endif\n")
        val ref = ifDirective(file).references.firstOrNull { it.canonicalText == "Foo" }
        assertNotNull("Expected an expression reference 'Foo' on #if", ref)
        assertEquals("Foo", (ref!!.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName())
    }

    fun testBooleanWordIsNotAReference() {
        val file = setup("#if yes\n#endif\n")
        assertTrue("A boolean word must not become a reference", ifDirective(file).references.isEmpty())
    }

    fun testReferencesSearchFindsIfUsage() {
        val file = setup("#define A 1\n#if A\n#endif\n")
        val a = define(file, "A")
        val refs = ReferencesSearch.search(a).findAll()
        assertTrue("Find Usages must include the #if usage of A", refs.any { it.canonicalText == "A" })
    }

    fun testRenameDefineUpdatesIfReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define A<caret> 1\n#if A\n#endif\n[Setup]\nX={#A}\n"
        )
        myFixture.renameElementAtCaret("B")
        myFixture.checkResult("#define B 1\n#if B\n#endif\n[Setup]\nX={#B}\n")
    }
}

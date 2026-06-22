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
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine

/**
 * Tests that identifiers inside a `#pragma` expression argument behave like references to `#define`s:
 * they resolve, take part in Find Usages and rename, exactly like references inside a `#define` body.
 */
class IsPreprocessorPragmaReferenceTest : BasePlatformTestCase() {

    private fun setup(content: String): IsScriptFile {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        if (file is IsScriptFile) return file
        return InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IsScriptFile
    }

    private fun allDirectives(file: IsScriptFile): List<IsPreprocessorDirective> {
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

    private fun findPragma(file: IsScriptFile): IsPreprocessorDirective =
        allDirectives(file).first { (it as? IsPreprocessorDirectiveEx)?.isPragma() == true }

    private fun findDefine(file: IsScriptFile, name: String): IsPreprocessorDirective =
        allDirectives(file).first { (it as? IsPreprocessorDirectiveEx)?.getDefineName() == name }

    fun testReferenceInPragmaExpressionResolvesToDefine() {
        val file = setup("#define Msg \"hi\"\n#pragma message Msg + \"!\"\n")
        val pragma = findPragma(file)
        val ref = pragma.references.firstOrNull { it.canonicalText == "Msg" }
        assertNotNull("Expected an expression reference 'Msg' on the #pragma", ref)
        assertEquals("Msg", (ref!!.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName())
    }

    fun testFlagPragmaHasNoReferences() {
        // The letters of -v+ / -c- are flags, not #define references.
        val file = setup("#define v 1\n#pragma option -v+\n")
        val pragma = findPragma(file)
        assertTrue("A flag pragma must not create references", pragma.references.isEmpty())
    }

    fun testReferencesSearchFindsPragmaUsage() {
        val file = setup("#define A 1\n#pragma verboselevel A\n")
        val a = findDefine(file, "A")
        val refs = ReferencesSearch.search(a).findAll()
        assertTrue(
            "Find Usages must include the usage of A inside the #pragma",
            refs.any { it.canonicalText == "A" }
        )
    }

    fun testRenameDefineUpdatesPragmaReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Msg<caret> \"hi\"\n#pragma message Msg\n"
        )
        myFixture.renameElementAtCaret("Greeting")
        myFixture.checkResult("#define Greeting \"hi\"\n#pragma message Greeting\n")
    }

    fun testDefineNameSuggestedInPragmaExpression() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#define Greeting \"hi\"\n#pragma message Gr<caret>\n")
        myFixture.completeBasic()
        // When 'Greeting' is the only match the platform auto-inserts it (lookup list becomes null);
        // otherwise it appears among the offered variants. Accept either outcome.
        val variants = myFixture.lookupElementStrings ?: emptyList()
        val inserted = myFixture.editor.document.text.contains("message Greeting")
        assertTrue(
            "Expected 'Greeting' #define suggestion in pragma expression, variants=$variants",
            "Greeting" in variants || inserted
        )
    }
}

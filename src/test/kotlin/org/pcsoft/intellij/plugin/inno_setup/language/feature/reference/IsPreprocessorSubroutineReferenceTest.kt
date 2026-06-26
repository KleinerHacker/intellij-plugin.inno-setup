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
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine

/**
 * Tests references for `#sub` subroutine names (resolved from a `#for` body) and `#for` loop variables
 * (resolved locally within the loop, renameable, and not leaking into following directives).
 */
class IsPreprocessorSubroutineReferenceTest : IsTimedBasePlatformTestCase() {

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

    private fun forDirective(file: IsScriptFile) =
        directives(file).first { (it as IsPreprocessorDirectiveEx).isFor() }

    fun testForBodyResolvesToSub() {
        val file = setup("#sub Worker\n#endsub\n#for {i = 0; i < 1; i++} Worker\n")
        val ref = forDirective(file).references.firstOrNull { it.canonicalText == "Worker" }
        assertNotNull("Expected a body reference 'Worker' on #for", ref)
        assertEquals("Worker", (ref!!.resolve() as? IsPreprocessorDirectiveEx)?.getSubroutineName())
    }

    fun testLoopVariableReferenceResolvesLocally() {
        val file = setup("#for {i = 0; i < 1; i++} 0\n[Setup]\nAppName=T\n")
        val forDir = forDirective(file)
        val ref = forDir.references.firstOrNull {
            it is IsPreprocessorForVariableReference && it.canonicalText == "i"
        }
        assertNotNull("Expected a local loop-variable reference 'i'", ref)
        assertSame("The loop variable must resolve to its own #for directive", forDir, ref!!.resolve())
    }

    fun testRenameLoopVariableUpdatesAllSlots() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#for {i<caret> = 0; i < 10; i++} 0\n[Setup]\nAppName=T\n"
        )
        myFixture.renameElementAtCaret("j")
        myFixture.checkResult("#for {j = 0; j < 10; j++} 0\n[Setup]\nAppName=T\n")
    }

    fun testRenameSubUpdatesForBodyReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#sub Worker<caret>\n#endsub\n#for {i = 0; i < 1; i++} Worker\n[Setup]\nAppName=T\n"
        )
        myFixture.renameElementAtCaret("Runner")
        myFixture.checkResult("#sub Runner\n#endsub\n#for {i = 0; i < 1; i++} Runner\n[Setup]\nAppName=T\n")
    }

    fun testLoopVariableDoesNotLeakToFollowingDirective() {
        // `i` used in a later #define must not resolve to the #for loop variable (scope = the #for only).
        val file = setup("#for {i = 0; i < 1; i++} 0\n#define X i\n[Setup]\nAppName=T\n")
        val define = directives(file).first { (it as IsPreprocessorDirectiveEx).getDefineName() == "X" }
        val ref = define.references.firstOrNull { it.canonicalText == "i" }
        assertNotNull("The #define still has an 'i' reference", ref)
        assertNull("The loop variable must not be visible outside its #for", ref!!.resolve())
    }
}

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
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference.IsPreprocessorMacroParameterElement
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference.IsPreprocessorMacroParameterReference
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests the references of function-like macro parameters used inside the macro body: they resolve to the
 * parameter declaration of the same `#define`, stay confined to that directive, never conflate with the macro
 * name, and support rename.
 */
class IsPreprocessorMacroParameterReferenceTest : IsTimedBasePlatformTestCase() {

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
                        dirs.addAll(
                            PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsPreprocessorDirective::class.java)
                        )
                }
                dirs
            }
    }

    private fun macro(file: IsScriptFile, name: String): IsPreprocessorDirective =
        directives(file).first { (it as IsPreprocessorDirectiveEx).getDefineName() == name }

    /**
     * A parameter used in the macro body carries its own reference kind, so it is never resolved against the
     * `#define`s of the file (which would report it as unresolved).
     */
    fun testParameterUseInBodyIsAParameterReference() {
        val file = setup("#define Twice(A) A * 2\n[Setup]\nAppName=T\n")
        val ref = macro(file, "Twice").references
            .firstOrNull { it is IsPreprocessorMacroParameterReference && it.canonicalText == "A" }
        assertNotNull("The body use of 'A' must be a macro-parameter reference", ref)
    }

    /**
     * The reference resolves to the parameter declaration inside the same `#define` — a dedicated element, not
     * the directive itself, so renaming the parameter can never rename the macro.
     */
    fun testParameterReferenceResolvesToItsDeclaration() {
        val file = setup("#define Twice(A) A * 2\n[Setup]\nAppName=T\n")
        val ref = macro(file, "Twice").references
            .first { it is IsPreprocessorMacroParameterReference && it.canonicalText == "A" }
        val target = ref.resolve()
        assertTrue("Expected a macro parameter declaration", target is IsPreprocessorMacroParameterElement)
        assertEquals("A", (target as IsPreprocessorMacroParameterElement).name)
    }

    /**
     * A declared parameter type (`int A`) must not end up in the parameter name, otherwise neither the
     * reference nor the completion would match the identifier written in the body.
     */
    fun testTypedParameterResolvesByItsNameOnly() {
        val file = setup("#define Twice(int A) A * 2\n[Setup]\nAppName=T\n")
        val dex = macro(file, "Twice") as IsPreprocessorDirectiveEx
        assertEquals(listOf("A"), dex.getMacroParameters())
        val ref = macro(file, "Twice").references
            .firstOrNull { it is IsPreprocessorMacroParameterReference && it.canonicalText == "A" }
        assertNotNull("A typed parameter must still be referenceable in the body", ref)
    }

    /**
     * The declaration inside the `(…)` list is the declaration, not a use — it must not produce a reference of
     * its own, which would make the parameter reference itself.
     */
    fun testDeclarationItselfIsNoReference() {
        val file = setup("#define Twice(A) A * 2\n[Setup]\nAppName=T\n")
        val refs = macro(file, "Twice").references
            .filter { it is IsPreprocessorMacroParameterReference && it.canonicalText == "A" }
        assertEquals("Only the body use is a reference", 1, refs.size)
    }

    /**
     * The parameter is scoped to its own `#define`: the same name used in a later directive must not resolve
     * to it.
     */
    fun testParameterDoesNotLeakToFollowingDirective() {
        val file = setup("#define Twice(A) A * 2\n#define X A\n[Setup]\nAppName=T\n")
        val ref = macro(file, "X").references.firstOrNull { it.canonicalText == "A" }
        assertNotNull("The following #define still has an 'A' reference", ref)
        assertNull("A macro parameter must not be visible outside its own #define", ref!!.resolve())
    }

    /** Renaming a parameter rewrites the declaration and every use inside the body — and nothing else. */
    fun testRenameParameterUpdatesDeclarationAndBody() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Twice(A) A<caret> * 2 + A\n[Setup]\nAppName=T\n"
        )
        myFixture.renameElementAtCaret("Value")
        myFixture.checkResult("#define Twice(Value) Value * 2 + Value\n[Setup]\nAppName=T\n")
    }

    /** Find Usages on a parameter lists its uses in the body — and only those. */
    fun testFindUsagesFindsTheBodyUses() {
        val file = setup("#define Twice(A) A * 2 + A\n#define Other A\n[Setup]\nAppName=T\n")
        val declaration = macro(file, "Twice").references
            .filterIsInstance<IsPreprocessorMacroParameterReference>()
            .first()
            .resolve()!!
        val usages = ReferencesSearch.search(declaration).findAll()
        assertEquals("Both body uses must be found, and nothing outside the macro", 2, usages.size)
    }

    /** Renaming the macro itself leaves its parameters untouched. */
    fun testRenamingMacroKeepsParameterNames() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Twice<caret>(A) A * 2\n#define Y Twice(2)\n[Setup]\nAppName=T\n"
        )
        myFixture.renameElementAtCaret("Doubled")
        myFixture.checkResult("#define Doubled(A) A * 2\n#define Y Doubled(2)\n[Setup]\nAppName=T\n")
    }
}

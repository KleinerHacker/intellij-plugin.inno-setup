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
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests that the identifier of an `#ifdef`/`#ifndef` references the matching `#define` (navigation, rename
 * and Find Usages) — but, unlike a `#if` condition, a missing `#define` produces **no error** (an `#ifdef`
 * on an undefined macro is legitimate).
 */
class IsPreprocessorIfdefReferenceTest : IsTimedBasePlatformTestCase() {

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
                        dirs.addAll(
                            PsiTreeUtil.getChildrenOfTypeAsList(
                                injectedPsi,
                                IsPreprocessorDirective::class.java
                            )
                        )
                }
                dirs
            }
    }

    private fun ifdefDirective(file: IsScriptFile) =
        directives(file).first { (it as IsPreprocessorDirectiveEx).isIfdefFamily() }

    private fun define(file: IsScriptFile, name: String) =
        directives(file).first { (it as IsPreprocessorDirectiveEx).getDefineName() == name }

    fun testIfdefReferenceResolvesToPrecedingDefine() {
        val file = setup("#define Foo 1\n#ifdef Foo\n#endif\n$setupTail")
        val ref = ifdefDirective(file).references.firstOrNull { it.canonicalText == "Foo" }
        assertNotNull("Expected a reference 'Foo' on #ifdef", ref)
        assertEquals("Foo", (ref!!.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName())
    }

    fun testIfndefReferenceResolvesToPrecedingDefine() {
        val file = setup("#define Foo 1\n#ifndef Foo\n#endif\n$setupTail")
        val ref = ifdefDirective(file).references.firstOrNull { it.canonicalText == "Foo" }
        assertNotNull("Expected a reference 'Foo' on #ifndef", ref)
        assertEquals("Foo", (ref!!.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName())
    }

    fun testIfdefOnUndefinedNameIsNotAnError() {
        val errors =
            myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, "#ifdef Missing\n#endif\n$setupTail") }
                .doHighlighting()
                .filter { it.severity.name == "ERROR" }
        assertTrue(
            "#ifdef on an undefined macro must not produce an error, was: ${errors.map { it.description }}",
            errors.isEmpty()
        )
    }

    fun testIfdefWithoutNameIsError() {
        val errors = myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, "#ifdef\n#endif\n$setupTail") }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" && it.description?.contains("requires an identifier") == true }
        assertTrue("#ifdef without an identifier must be an error", errors.isNotEmpty())
    }

    private fun singleIdentifierErrors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter { it.severity.name == "ERROR" && it.description?.contains("requires a single identifier") == true }

    fun testIfdefWithExpressionIsError() {
        assertTrue(
            "#ifdef with an expression must be flagged",
            singleIdentifierErrors("#ifdef Foo+Bar\n#endif\n$setupTail").isNotEmpty()
        )
    }

    fun testIfdefWithNumberLiteralIsError() {
        assertTrue(
            "#ifdef with a number must be flagged",
            singleIdentifierErrors("#ifdef 1+1\n#endif\n$setupTail").isNotEmpty()
        )
    }

    fun testIfdefWithStringLiteralIsError() {
        assertTrue(
            "#ifdef with a string must be flagged",
            singleIdentifierErrors("#ifdef \"x\"\n#endif\n$setupTail").isNotEmpty()
        )
    }

    fun testIfdefWithMultipleTokensIsError() {
        assertTrue(
            "#ifdef with two names must be flagged",
            singleIdentifierErrors("#ifdef Foo Bar\n#endif\n$setupTail").isNotEmpty()
        )
    }

    fun testIfndefWithExpressionIsError() {
        assertTrue(
            "#ifndef with an expression must be flagged",
            singleIdentifierErrors("#ifndef Foo*2\n#endif\n$setupTail").isNotEmpty()
        )
    }

    fun testIfdefWithPlainIdentifierIsNotASingleIdentifierError() {
        assertTrue(
            "A plain identifier must not raise the single-identifier error",
            singleIdentifierErrors("#ifdef Foo\n#endif\n$setupTail").isEmpty()
        )
    }

    fun testReferencesSearchFindsIfdefUsage() {
        val file = setup("#define A 1\n#ifdef A\n#endif\n$setupTail")
        val a = define(file, "A")
        val refs = ReferencesSearch.search(a).findAll()
        assertTrue("Find Usages must include the #ifdef usage of A", refs.any { it.canonicalText == "A" })
    }

    fun testRenameDefineUpdatesIfdefReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define A<caret> 1\n#ifdef A\n#endif\n[Setup]\nX={#A}\n"
        )
        myFixture.renameElementAtCaret("B")
        myFixture.checkResult("#define B 1\n#ifdef B\n#endif\n[Setup]\nX={#B}\n")
    }
}

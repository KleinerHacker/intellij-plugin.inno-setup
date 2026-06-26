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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.computeArrayElementValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.IsPreprocessorExprValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests `#dim`/`#redim` parsing, the `#dim`/`#define` interplay, static element value resolution and
 * `#dim` ↔ usage reference resolution / rename.
 */
class IsSectionPreprocessorArrayTest : IsTimedBasePlatformTestCase() {

    private fun setup(content: String): IsScriptFile {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        if (file is IsScriptFile) return file
        return InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IsScriptFile
    }

    private fun IsScriptFile.directives(): List<IsPreprocessorDirectiveEx> {
        val mgr = InjectedLanguageManager.getInstance(project)
        return PsiTreeUtil.getChildrenOfTypeAsList(this, IsSectionPreprocessorLine::class.java)
            .flatMap { line ->
                val dirs = mutableListOf<IsPreprocessorDirective>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsPreprocessorFile)
                        dirs += PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsPreprocessorDirective::class.java)
                }
                dirs
            }
            .mapNotNull { it as? IsPreprocessorDirectiveEx }
    }

    // ── parsing ─────────────────────────────────────────────────────────────────

    fun testDimNameAndSize() {
        val dim = setup("#dim Servers[3]\n").directives().first { it.isDim() }
        assertEquals("Servers", dim.getArrayName())
        assertEquals("3", dim.getArraySizeText())
        assertTrue(dim.isArrayDeclaration())
    }

    fun testDimWithScope() {
        val dim = setup("#dim public Foo[2]\n").directives().first { it.isDim() }
        assertEquals("Foo", dim.getArrayName())
        assertNotNull("scope keyword must be recognised", dim.getVisibilityIdentifier())
    }

    fun testDimInlineInitializer() {
        val dim = setup("#dim A[3] {1, 2, 3}\n").directives().first { it.isDim() }
        assertEquals("1, 2, 3", dim.getArrayInitializerText())
    }

    fun testRedimRecognised() {
        val redim = setup("#dim A[1]\n#redim A[5]\n").directives().first { it.isRedim() }
        assertEquals("A", redim.getArrayName())
        assertEquals("5", redim.getArraySizeText())
    }

    fun testArrayElementDefine() {
        val def = setup("#dim A[3]\n#define A[0] 10\n").directives().first { it.isArrayElementDefine() }
        assertEquals("A", def.getDefineArrayName())
        assertEquals("0", def.getDefineArrayIndexText())
        assertEquals("10", def.getDefineExpressionText())
    }

    fun testArrayElementDefineIsNotScalarDefine() {
        // A plain scalar #define is still recognised normally next to an element define.
        val dirs = setup("#dim A[3]\n#define A[0] 10\n#define B 5\n").directives()
        val scalar = dirs.first { it.isDefine() && !it.isArrayElementDefine() }
        assertEquals("B", scalar.getDefineName())
    }

    // ── value resolution across #dim / #define ──────────────────────────────────

    fun testElementValueResolves() {
        val file = setup("#dim A[3]\n#define A[1] 42\n")
        assertEquals(IsPreprocessorExprValue.IntValue(42), file.computeArrayElementValue("A", 1))
    }

    fun testElementValueFromScalarDefine() {
        val file = setup("#define N 7\n#dim A[1]\n#define A[0] N * 2\n")
        assertEquals(IsPreprocessorExprValue.IntValue(14), file.computeArrayElementValue("A", 0))
    }

    fun testInlineInitializerValuesResolve() {
        val file = setup("#dim A[3] {1, 2, 3}\n")
        assertEquals(IsPreprocessorExprValue.IntValue(1), file.computeArrayElementValue("A", 0))
        assertEquals(IsPreprocessorExprValue.IntValue(2), file.computeArrayElementValue("A", 1))
        assertEquals(IsPreprocessorExprValue.IntValue(3), file.computeArrayElementValue("A", 2))
    }

    fun testUnassignedElementNotComputable() {
        val file = setup("#dim A[3]\n#define A[0] 5\n")
        assertNull(file.computeArrayElementValue("A", 2))
    }

    // ── references & rename ─────────────────────────────────────────────────────

    fun testRedimReferencesDim() {
        val redim = setup("#dim Servers[3]\n#redim Servers[5]\n").directives().first { it.isRedim() }
        val target = redim.references.firstNotNullOfOrNull { it.resolve() as? IsPreprocessorDirectiveEx }
        assertEquals("Servers", target?.getArrayName())
        assertTrue("a #redim must resolve to a #dim", target?.isDim() == true)
    }

    fun testElementDefineReferencesDim() {
        val def = setup("#dim Servers[3]\n#define Servers[0] 1\n").directives().first { it.isArrayElementDefine() }
        val target = def.references
            .mapNotNull { it.resolve() as? IsPreprocessorDirectiveEx }
            .firstOrNull { it.isDim() }
        assertEquals("Servers", target?.getArrayName())
    }

    fun testRenameDimUpdatesAllUsages() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#dim Ser<caret>vers[3]\n#define Servers[0] \"a\"\n#redim Servers[5]\n#define N DimOf(Servers)\n"
        )
        myFixture.renameElementAtCaret("Hosts")
        val text = InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file).text
        assertFalse("old array name must be gone", text.contains("Servers"))
        assertTrue("#dim renamed", text.contains("#dim Hosts[3]"))
        assertTrue("element define renamed", text.contains("#define Hosts[0]"))
        assertTrue("#redim renamed", text.contains("#redim Hosts[5]"))
        assertTrue("DimOf usage renamed", text.contains("DimOf(Hosts)"))
    }

    fun testRenameDimLeavesSimilarScalarDefineUntouched() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Servers 1\n#dim Hos<caret>ts[2]\n#define Hosts[0] Servers\n"
        )
        myFixture.renameElementAtCaret("Nodes")
        val text = InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file).text
        assertTrue("scalar #define Servers must be untouched", text.contains("#define Servers 1"))
        assertTrue("array renamed", text.contains("#dim Nodes[2]"))
        assertTrue("element define array name renamed", text.contains("#define Nodes[0] Servers"))
    }

    // ── completion ──────────────────────────────────────────────────────────────

    fun testRedimOffersArrayNames() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#dim Servers[3]\n#redim <caret>\n")
        myFixture.completeBasic()
        assertTrue("#redim must offer existing array names", "Servers" in (myFixture.lookupElementStrings ?: emptyList()))
    }

    fun testDimOffersScopeKeywords() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#dim <caret>\n")
        myFixture.completeBasic()
        assertTrue("#dim must offer scope keywords", "public" in (myFixture.lookupElementStrings ?: emptyList()))
    }

    fun testArrayNameOfferedInExpression() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#dim Servers[3]\n#define X <caret>\n")
        myFixture.completeBasic()
        assertTrue("array name must be offered in expressions", "Servers" in (myFixture.lookupElementStrings ?: emptyList()))
    }
}

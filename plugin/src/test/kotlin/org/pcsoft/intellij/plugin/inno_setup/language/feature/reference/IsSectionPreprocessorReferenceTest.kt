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
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.reference.IsSectionPreprocessorConstantReference
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionConstantBody
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

class IsSectionPreprocessorReferenceTest : IsTimedBasePlatformTestCase() {

    private fun setup(content: String): IsScriptFile {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        if (file is IsScriptFile) return file
        // In IntelliJ 2025.3, configureByText may return the injected IsPreprocessorFile when
        // the content starts with a preprocessor line. Use getTopLevelFile to recover.
        return InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IsScriptFile
    }

    private fun findIsppConstantBody(file: IsScriptFile): IsSectionConstantBody? =
        PsiTreeUtil.findChildrenOfType(file, IsSectionConstantBody::class.java)
            .firstOrNull { it.text.startsWith("#") }

    private fun findDefine(file: IsScriptFile, name: String): IsPreprocessorDirective? {
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
            .firstOrNull { (it as? IsPreprocessorDirectiveEx)?.getDefineName() == name }
    }

    // ── resolve ──────────────────────────────────────────────────────────────

    fun testIsppConstantResolvesToDefine() {
        val file = setup("#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n")
        val body = findIsppConstantBody(file)
        assertNotNull("Expected IsSectionConstantBody for {#AppVersion}", body)

        val ref = IsSectionPreprocessorConstantReference(body!!, "AppVersion")
        val resolved = ref.resolve()

        assertNotNull("Expected {#AppVersion} to resolve to #define directive", resolved)
        assertInstanceOf(resolved, IsPreprocessorDirective::class.java)
        assertEquals("AppVersion", (resolved as IsPreprocessorDirectiveEx).getDefineName())
    }

    fun testUnknownIsppConstantDoesNotResolve() {
        val file = setup("[Files]\nSource: \"app.exe\"; DestDir: \"{#Unknown}\"\n")
        val body = findIsppConstantBody(file)
        assertNotNull("Expected IsSectionConstantBody for {#Unknown}", body)

        val ref = IsSectionPreprocessorConstantReference(body!!, "Unknown")
        assertNull("Unknown ISPP constant should not resolve", ref.resolve())
    }

    // ── isReferenceTo ────────────────────────────────────────────────────────

    fun testIsReferenceToDirective() {
        val file = setup("#define MyVar \"value\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#MyVar}\"\n")
        val body = findIsppConstantBody(file)!!
        val directive = findDefine(file, "MyVar")
        assertNotNull("Expected #define MyVar directive", directive)

        val ref = IsSectionPreprocessorConstantReference(body, "MyVar")
        assertTrue(
            "isReferenceTo should return true for the directive element",
            ref.isReferenceTo(directive!!)
        )
    }

    fun testIsReferenceToNameIdentifier() {
        val file = setup("#define MyVar \"value\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#MyVar}\"\n")
        val body = findIsppConstantBody(file)!!
        val directive = findDefine(file, "MyVar")
        val nameId = (directive as? IsPreprocessorDirectiveEx)?.nameIdentifier
        assertNotNull("Expected name identifier on #define MyVar", nameId)

        val ref = IsSectionPreprocessorConstantReference(body, "MyVar")
        assertTrue(
            "isReferenceTo should return true when IntelliJ passes the name identifier",
            ref.isReferenceTo(nameId!!)
        )
    }

    fun testIsReferenceToWrongNameReturnsFalse() {
        val file =
            setup("#define MyVar \"value\"\n#define Other \"x\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#MyVar}\"\n")
        val body = findIsppConstantBody(file)!!
        val otherDirective = findDefine(file, "Other")
        assertNotNull("Expected #define Other directive", otherDirective)

        val ref = IsSectionPreprocessorConstantReference(body, "MyVar")
        assertFalse(
            "isReferenceTo should return false for a different directive",
            ref.isReferenceTo(otherDirective!!)
        )
    }

    // ── define name extraction ───────────────────────────────────────────────

    fun testDefineNameExtracted() {
        val file = setup("#define MyConst \"hello\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        val directive = findDefine(file, "MyConst")
        assertNotNull("Expected #define directive", directive)
        assertEquals("MyConst", (directive as IsPreprocessorDirectiveEx).getDefineName())
    }

    // ── rename ───────────────────────────────────────────────────────────────

    fun testRenameDefineUpdatesConstantReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define App<caret>Version \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        )
        myFixture.renameElementAtCaret("NewVersion")
        myFixture.checkResult(
            "#define NewVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#NewVersion}\"\n"
        )
    }

    fun testRenameDefineUpdatesMultipleReferences() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define App<caret>Name \"MyApp\"\n[Setup]\nAppName={#AppName}\nAppPublisher={#AppName}\n"
        )
        myFixture.renameElementAtCaret("ProductName")
        myFixture.checkResult(
            "#define ProductName \"MyApp\"\n[Setup]\nAppName={#ProductName}\nAppPublisher={#ProductName}\n"
        )
    }

    // ── #define expression references (free text → other #define) ─────────────

    fun testExpressionReferenceResolvesToEarlierDefine() {
        val file = setup("#define Base 10\n#define Total Base\n")
        val total = findDefine(file, "Total")!!
        val ref = total.references.firstOrNull { it.canonicalText == "Base" }
        assertNotNull("Expected an expression reference 'Base' on #define Total", ref)
        assertEquals("Base", (ref!!.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName())
    }

    fun testForwardExpressionReferenceDoesNotResolve() {
        // Base is defined *after* Total → must not resolve (declaration order enforced).
        val file = setup("#define Total Base\n#define Base 10\n")
        val total = findDefine(file, "Total")!!
        val ref = total.references.firstOrNull { it.canonicalText == "Base" }
        assertNotNull("Expected an expression reference 'Base' on #define Total", ref)
        assertNull("A forward reference must not resolve", ref!!.resolve())
    }

    fun testNumbersAndStringsAreNotExpressionReferences() {
        val file = setup("#define B 1 + \"x\"\n")
        val b = findDefine(file, "B")!!
        assertTrue("Numbers and strings must not create references", b.references.isEmpty())
    }

    fun testMacroParametersAreNotExpressionReferences() {
        val file = setup("#define Max(a, b) a > b ? a : b\n")
        val max = findDefine(file, "Max")!!
        val texts = max.references.map { it.canonicalText }
        assertEquals("Macro parameters must not create references", emptyList<String>(), texts)
    }

    fun testMacroBodyReferencesOtherDefineButNotParams() {
        val file = setup("#define K 5\n#define F(x) x + K\n")
        val f = findDefine(file, "F")!!
        val texts = f.references.map { it.canonicalText }.toSet()
        assertEquals("Only the real define 'K' is a reference, not the parameter 'x'", setOf("K"), texts)
        assertEquals("K", (f.references.first().resolve() as? IsPreprocessorDirectiveEx)?.getDefineName())
    }

    fun testReferencesSearchFindsExpressionUsage() {
        val file = setup("#define A 1\n#define B A + 2\n")
        val a = findDefine(file, "A")!!
        val refs = ReferencesSearch.search(a).findAll()
        assertTrue(
            "Find Usages must include the expression usage of A inside B",
            refs.any { it.canonicalText == "A" })
    }

    fun testRenameDefineUpdatesExpressionReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define A<caret> 1\n#define B A + 2\n[Setup]\nX={#B}\n"
        )
        myFixture.renameElementAtCaret("C")
        myFixture.checkResult(
            "#define C 1\n#define B C + 2\n[Setup]\nX={#B}\n"
        )
    }

    // ── #undef references the preceding #define ───────────────────────────────

    private fun findUndef(file: IsScriptFile, name: String): IsPreprocessorDirective? {
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
            .firstOrNull { (it as? IsPreprocessorDirectiveEx)?.isUndef() == true && it.name == name }
    }

    fun testUndefResolvesToPrecedingDefine() {
        val file = setup("#define Foo 1\n#undef Foo\n")
        val undef = findUndef(file, "Foo")!!
        val ref = undef.references.firstOrNull { it.canonicalText == "Foo" }
        assertNotNull("Expected an #undef reference 'Foo'", ref)
        assertEquals("Foo", (ref!!.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName())
    }

    fun testForwardUndefDoesNotResolve() {
        // Foo is defined *after* the #undef → must not resolve (declaration order enforced).
        val file = setup("#undef Foo\n#define Foo 1\n")
        val undef = findUndef(file, "Foo")!!
        val ref = undef.references.firstOrNull { it.canonicalText == "Foo" }
        assertNotNull("Expected an #undef reference 'Foo'", ref)
        assertNull("A forward #undef must not resolve", ref!!.resolve())
    }

    fun testRenameDefineUpdatesUndef() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Ap<caret>p 1\n#undef App\n[Setup]\nX={#App}\n"
        )
        myFixture.renameElementAtCaret("Tool")
        myFixture.checkResult("#define Tool 1\n#undef Tool\n[Setup]\nX={#Tool}\n")
    }

    fun testReferencesSearchFindsUndefUsage() {
        val file = setup("#define A 1\n#undef A\n")
        val a = findDefine(file, "A")!!
        val refs = ReferencesSearch.search(a).findAll()
        assertTrue("Find Usages must include the #undef usage of A", refs.any { it.canonicalText == "A" })
    }

    // ── optional visibility/scope keyword ─────────────────────────────────────

    fun testVisibilityDefineNameIsTheNameNotTheKeyword() {
        val file = setup("#define public MyConst 1\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        val directive = findDefine(file, "MyConst")
        assertNotNull("Expected #define with scope keyword to expose 'MyConst' as the name", directive)
        assertEquals("MyConst", (directive as IsPreprocessorDirectiveEx).getDefineName())
        assertEquals("public", directive.getVisibilityIdentifier()?.text)
    }

    fun testVisibilityDefineResolvesConstantReference() {
        val file = setup("#define private AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#AppVersion}\"\n")
        val body = findIsppConstantBody(file)!!
        val ref = IsSectionPreprocessorConstantReference(body, "AppVersion")
        assertEquals(
            "AppVersion",
            (ref.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName()
        )
    }

    fun testUndefWithVisibilityResolvesToDefine() {
        val file = setup("#define Foo 1\n#undef protected Foo\n")
        val undef = findUndef(file, "Foo")!!
        assertEquals("protected", (undef as IsPreprocessorDirectiveEx).getVisibilityIdentifier()?.text)
        val ref = undef.references.firstOrNull { it.canonicalText == "Foo" }
        assertEquals("Foo", (ref?.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName())
    }
}

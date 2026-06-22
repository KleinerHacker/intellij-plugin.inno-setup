/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine

/**
 * Tests for all supported forms of #define, covering:
 *   - name extraction  (getDefineName)
 *   - value extraction (definedConstants)
 *   - reference resolution & annotator (no "Unknown constant" error)
 *   - completion (name appears in {#} popup; preceding defines in expressions)
 *   - rename (name + all {#Name} usages updated)
 *   - annotator validation: function-like macros require an expression
 */
class IsSectionPreprocessorDefineTest : BasePlatformTestCase() {

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun setup(content: String): IsScriptFile {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        if (file is IsScriptFile) return file
        return InjectedLanguageManager
            .getInstance(project).getTopLevelFile(file) as IsScriptFile
    }

    private fun IsScriptFile.firstDefine(): IsPreprocessorDirectiveEx? {
        val mgr = InjectedLanguageManager.getInstance(project)
        return PsiTreeUtil.getChildrenOfTypeAsList(this, IsSectionPreprocessorLine::class.java)
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
            .firstOrNull { (it as? IsPreprocessorDirectiveEx)?.isDefine() == true }
            ?.let { it as? IsPreprocessorDirectiveEx }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 1.  Name extraction — getDefineName()
    // ═══════════════════════════════════════════════════════════════════════════

    fun testGetDefineNameSimple() {
        val file = setup("#define MyConst\n")
        assertEquals("MyConst", file.firstDefine()?.getDefineName())
    }

    fun testGetDefineNameWithValue() {
        val file = setup("#define MyConst SimpleValue\n")
        assertEquals("MyConst", file.firstDefine()?.getDefineName())
    }

    fun testGetDefineNameWithQuotedValue() {
        val file = setup("#define MyConst \"Quoted String\"\n")
        assertEquals("MyConst", file.firstDefine()?.getDefineName())
    }

    fun testGetDefineNameWithMultiWordValue() {
        val file = setup("#define MyConst Multi Word Value\n")
        assertEquals("MyConst", file.firstDefine()?.getDefineName())
    }

    fun testGetDefineNameFunctionLikeMacro() {
        val file = setup("#define Max(a,b) ((a)>(b)?(a):(b))\n")
        assertEquals("Max", file.firstDefine()?.getDefineName())
    }

    fun testGetDefineNameFunctionLikeMacroNoParams() {
        val file = setup("#define Counter() 0\n")
        assertEquals("Counter", file.firstDefine()?.getDefineName())
    }

    fun testGetDefineNameFunctionMacroSingleParam() {
        val file = setup("#define Square(x) x * x\n")
        assertEquals("Square", file.firstDefine()?.getDefineName())
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 3.  Value extraction — definedConstants()
    // ═══════════════════════════════════════════════════════════════════════════

    private fun IsScriptFile.constantValue(name: String) =
        definedConstants.firstOrNull { it.first == name }?.second

    fun testValueNoneIsNull() {
        val file = setup("#define MyConst\n")
        val pair = file.definedConstants.firstOrNull()
        assertNotNull("Entry must exist", pair)
        assertNull("Name-only define must have null value", pair!!.second)
    }

    fun testValueSimpleString() {
        val file = setup("#define MyConst Hello\n")
        assertEquals("Hello", file.constantValue("MyConst"))
    }

    fun testValueQuotedString() {
        val file = setup("#define MyConst \"Hello World\"\n")
        assertEquals("Hello World", file.constantValue("MyConst"))
    }

    fun testValueMultiWord() {
        val file = setup("#define AppTitle My Great App\n")
        assertEquals("My Great App", file.constantValue("AppTitle"))
    }

    fun testValueMacroIsNull() {
        val file = setup("#define Max(a,b) ((a)>(b)?(a):(b))\n")
        val pair = file.definedConstants.firstOrNull()
        assertNotNull("Macro entry must exist", pair)
        assertEquals("Macro name must be 'Max'", "Max", pair!!.first)
        assertNull("Function-like macro must have null value", pair.second)
    }

    fun testValueMacroNoParamsIsNull() {
        val file = setup("#define Counter() 0\n")
        val pair = file.definedConstants.firstOrNull()
        assertNotNull(pair)
        assertEquals("Counter", pair!!.first)
        assertNull(pair.second)
    }

    fun testValueIntegerExpression() {
        val file = setup("#define MyInt 42\n")
        assertEquals("42", file.constantValue("MyInt"))
    }

    fun testValueArithmeticExpression() {
        val file = setup("#define MyCalc 10 + 5 * 2\n")
        assertEquals("10 + 5 * 2", file.constantValue("MyCalc"))
    }

    fun testValueReferenceExpression() {
        val file = setup("#define Base 10\n#define Total Base + 5\n")
        assertEquals("Base + 5", file.constantValue("Total"))
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 4.  Reference resolution — {#Name} → #define
    // ═══════════════════════════════════════════════════════════════════════════

    private fun resolveAt(content: String): String? {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        // The caret is in {#Name} context (not in a preprocessor line), so
        // myFixture.file should be the IsScriptFile here.
        val issFile = run {
            val f = myFixture.file
            if (f is IsScriptFile) f
            else InjectedLanguageManager.getInstance(project).getTopLevelFile(f) as IsScriptFile
        }
        val ref = issFile.findReferenceAt(myFixture.caretOffset) ?: return null
        return (ref.resolve() as? IsPreprocessorDirectiveEx)?.getDefineName()
    }

    fun testSimpleDefineResolvesFromReference() {
        val name = resolveAt("#define AppName MyApp\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Name}\"\n")
        assertEquals("AppName", name)
    }

    fun testMacroResolvesFromReference() {
        val name = resolveAt("#define Max(a,b) body\n[Files]\nSource: \"a.exe\"; DestDir: \"{#Ma<caret>x}\"\n")
        assertEquals("Max", name)
    }

    fun testIntDefineResolvesFromReference() {
        val name = resolveAt("#define MyInt 42\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyI<caret>nt}\"\n")
        assertEquals("MyInt", name)
    }

    fun testStrDefineResolvesFromReference() {
        val name = resolveAt("#define MyStr \"hi\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MySt<caret>r}\"\n")
        assertEquals("MyStr", name)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 5.  Annotator — no "Unknown constant" error for any supported form
    // ═══════════════════════════════════════════════════════════════════════════

    private fun hasUnknownConstantError(content: String): Boolean {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return myFixture.doHighlighting().any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("Unknown constant", ignoreCase = true) == true
        }
    }

    fun testSimpleDefineNoAnnotatorError() {
        assertFalse(
            hasUnknownConstantError(
                "#define AppName MyApp\n[Files]\nSource: \"a.exe\"; DestDir: \"{#AppName}\"\n"
            )
        )
    }

    fun testMacroNoAnnotatorError() {
        assertFalse(
            hasUnknownConstantError(
                "#define Max(a,b) body\n[Files]\nSource: \"a.exe\"; DestDir: \"{#Max}\"\n"
            )
        )
    }

    fun testExpressionDefineNoAnnotatorError() {
        assertFalse(
            hasUnknownConstantError(
                "#define MyInt 42\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyInt}\"\n"
            )
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 6.  Completion — all define names appear in {#} popup
    // ═══════════════════════════════════════════════════════════════════════════

    fun testSimpleDefineAppearsInCompletion() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#<caret>}\"\n"
        )
        myFixture.completeBasic()
        assertTrue(
            "Simple define must appear in {#} completion",
            "AppVersion" in (myFixture.lookupElementStrings ?: emptyList())
        )
    }

    fun testMacroAppearsInCompletion() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Max(a,b) body\n#define Min(a,b) body\n[Files]\nSource: \"a.exe\"; DestDir: \"{#<caret>}\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertTrue("Macro name must appear in {#} completion", "Max" in variants)
        assertTrue("Macro name must appear in {#} completion", "Min" in variants)
    }

    fun testExpressionDefineAppearsInCompletion() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define MyVersion 1\n#define MyTitle \"T\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#<caret>}\"\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertTrue("Integer define must appear in {#} completion", "MyVersion" in variants)
        assertTrue("String define must appear in {#} completion", "MyTitle" in variants)
    }

    fun testExpressionCompletionOffersOnlyPrecedingDefines() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Alpha 1\n#define Beta 2\n#define Gamma <caret>\n#define Delta 4\n"
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertTrue("Preceding define Alpha must be offered", "Alpha" in variants)
        assertTrue("Preceding define Beta must be offered", "Beta" in variants)
        assertFalse("The current define Gamma must not be offered", "Gamma" in variants)
        assertFalse("A later define Delta must not be offered", "Delta" in variants)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 7.  Rename — declaration and all {#Name} usages updated
    // ═══════════════════════════════════════════════════════════════════════════

    fun testRenameSimpleDefine() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define App<caret>Name \"MyApp\"\n[Setup]\nAppName={#AppName}\n"
        )
        myFixture.renameElementAtCaret("ProductName")
        myFixture.checkResult(
            "#define ProductName \"MyApp\"\n[Setup]\nAppName={#ProductName}\n"
        )
    }

    fun testRenameMacroUpdatesDeclarationAndReferences() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Ma<caret>x(a,b) ((a)>(b)?(a):(b))\n[Files]\nSource: \"a.exe\"; DestDir: \"{#Max}\"\n"
        )
        myFixture.renameElementAtCaret("Biggest")
        myFixture.checkResult(
            "#define Biggest(a,b) ((a)>(b)?(a):(b))\n[Files]\nSource: \"a.exe\"; DestDir: \"{#Biggest}\"\n"
        )
    }

    fun testRenameIntDefine() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define MyI<caret>nt 42\n[Setup]\nAppVersion={#MyInt}\n"
        )
        myFixture.renameElementAtCaret("MyNumber")
        myFixture.checkResult(
            "#define MyNumber 42\n[Setup]\nAppVersion={#MyNumber}\n"
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 8.  Annotator — function-like macros require an expression
    // ═══════════════════════════════════════════════════════════════════════════

    private fun hasExpressionRequiredError(content: String): Boolean {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return myFixture.doHighlighting().any {
            it.severity == HighlightSeverity.ERROR &&
                    it.description?.contains("requires an expression", ignoreCase = true) == true
        }
    }

    fun testPlainDefineWithoutValueNoError() {
        assertFalse(
            "A plain define without a value is allowed",
            hasExpressionRequiredError("#define MyConst\n")
        )
    }

    fun testPlainDefineWithValueNoError() {
        assertFalse(
            "A plain define with a value is allowed",
            hasExpressionRequiredError("#define MyConst 42\n")
        )
    }

    fun testFunctionMacroWithBodyNoError() {
        assertFalse(
            "A function-like macro with an expression is valid",
            hasExpressionRequiredError("#define Max(a,b) a > b ? a : b\n")
        )
    }

    fun testFunctionMacroWithoutBodyProducesError() {
        assertTrue(
            "A function-like macro without an expression must produce an ERROR",
            hasExpressionRequiredError("#define Empty()\n")
        )
    }

    fun testFunctionMacroWithoutBodyWithParamsProducesError() {
        assertTrue(
            "A function-like macro with params but no expression must produce an ERROR",
            hasExpressionRequiredError("#define Broken(a, b)\n")
        )
    }
}

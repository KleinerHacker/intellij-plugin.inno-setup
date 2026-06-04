package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssIsppLine

/**
 * Tests for all supported forms of #define, covering:
 *   - name extraction  (getDefineName)
 *   - type extraction  (getDefineTypeName)
 *   - value extraction (definedConstants)
 *   - reference resolution & annotator (no "Unknown constant" error)
 *   - completion (name appears in {#} popup)
 *   - rename (name + all {#Name} usages updated)
 *   - annotator type validation for typed defines
 */
class IssPreprocessorDefineTest : BasePlatformTestCase() {

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun setup(content: String): IssFile {
        val file = myFixture.configureByText(IssFileType.INSTANCE, content)
        if (file is IssFile) return file
        return InjectedLanguageManager
            .getInstance(project).getTopLevelFile(file) as IssFile
    }

    private fun IssFile.firstDefine(): IsppDirectiveEx? {
        val mgr = InjectedLanguageManager.getInstance(project)
        return PsiTreeUtil.getChildrenOfTypeAsList(this, IssIsppLine::class.java)
            .flatMap { line ->
                val dirs = mutableListOf<IsppDirective>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsppFile)
                        dirs.addAll(PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsppDirective::class.java))
                }
                dirs
            }
            .firstOrNull { (it as? IsppDirectiveEx)?.isDefine() == true }
            ?.let { it as? IsppDirectiveEx }
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

    fun testGetDefineNameTypedInt() {
        val file = setup("#define int MyInt 42\n")
        assertEquals("MyInt", file.firstDefine()?.getDefineName())
    }

    fun testGetDefineNameTypedStr() {
        val file = setup("#define str MyStr \"hello\"\n")
        assertEquals("MyStr", file.firstDefine()?.getDefineName())
    }

    fun testGetDefineNameTypedFloat() {
        val file = setup("#define float MyPi 3.14\n")
        assertEquals("MyPi", file.firstDefine()?.getDefineName())
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 2.  Type extraction — getDefineTypeName()
    // ═══════════════════════════════════════════════════════════════════════════

    fun testGetDefineTypeNameSimple() {
        val file = setup("#define MyConst 42\n")
        assertNull("Simple define must not have a type", file.firstDefine()?.getDefineTypeName())
    }

    fun testGetDefineTypeNameMacro() {
        val file = setup("#define Max(a,b) body\n")
        assertNull("Function-like macro must not have a type", file.firstDefine()?.getDefineTypeName())
    }

    fun testGetDefineTypeNameInt() {
        val file = setup("#define int MyInt 0\n")
        assertEquals("int", file.firstDefine()?.getDefineTypeName())
    }

    fun testGetDefineTypeNameStr() {
        val file = setup("#define str MyStr \"x\"\n")
        assertEquals("str", file.firstDefine()?.getDefineTypeName())
    }

    fun testGetDefineTypeNameFloat() {
        val file = setup("#define float MyFloat 1.0\n")
        assertEquals("float", file.firstDefine()?.getDefineTypeName())
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 3.  Value extraction — definedConstants()
    // ═══════════════════════════════════════════════════════════════════════════

    private fun IssFile.constantValue(name: String) =
        definedConstants().firstOrNull { it.first == name }?.second

    fun testValueNoneIsNull() {
        val file = setup("#define MyConst\n")
        val pair = file.definedConstants().firstOrNull()
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
        val pair = file.definedConstants().firstOrNull()
        assertNotNull("Macro entry must exist", pair)
        assertEquals("Macro name must be 'Max'", "Max", pair!!.first)
        assertNull("Function-like macro must have null value", pair.second)
    }

    fun testValueMacroNoParamsIsNull() {
        val file = setup("#define Counter() 0\n")
        val pair = file.definedConstants().firstOrNull()
        assertNotNull(pair)
        assertEquals("Counter", pair!!.first)
        assertNull(pair.second)
    }

    fun testValueTypedIntExtracted() {
        val file = setup("#define int MyInt 42\n")
        assertEquals("42", file.constantValue("MyInt"))
    }

    fun testValueTypedStrExtracted() {
        val file = setup("#define str MyStr \"hello\"\n")
        assertEquals("hello", file.constantValue("MyStr"))
    }

    fun testValueTypedFloatExtracted() {
        val file = setup("#define float MyFloat 3.14\n")
        assertEquals("3.14", file.constantValue("MyFloat"))
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 4.  Reference resolution — {#Name} → #define
    // ═══════════════════════════════════════════════════════════════════════════

    private fun resolveAt(content: String): String? {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        // The caret is in {#Name} context (not in a preprocessor line), so
        // myFixture.file should be the IssFile here.
        val issFile = run {
            val f = myFixture.file
            if (f is IssFile) f
            else InjectedLanguageManager.getInstance(project).getTopLevelFile(f) as IssFile
        }
        val ref = issFile.findReferenceAt(myFixture.caretOffset) ?: return null
        return (ref.resolve() as? IsppDirectiveEx)?.getDefineName()
    }

    fun testSimpleDefineResolvesFromReference() {
        val name = resolveAt("#define AppName MyApp\n[Files]\nSource: \"a.exe\"; DestDir: \"{#App<caret>Name}\"\n")
        assertEquals("AppName", name)
    }

    fun testMacroResolvesFromReference() {
        val name = resolveAt("#define Max(a,b) body\n[Files]\nSource: \"a.exe\"; DestDir: \"{#Ma<caret>x}\"\n")
        assertEquals("Max", name)
    }

    fun testTypedIntDefineResolvesFromReference() {
        val name = resolveAt("#define int MyInt 42\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyI<caret>nt}\"\n")
        assertEquals("MyInt", name)
    }

    fun testTypedStrDefineResolvesFromReference() {
        val name = resolveAt("#define str MyStr \"hi\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MySt<caret>r}\"\n")
        assertEquals("MyStr", name)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 5.  Annotator — no "Unknown constant" error for any supported form
    // ═══════════════════════════════════════════════════════════════════════════

    private fun hasUnknownConstantError(content: String): Boolean {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        return myFixture.doHighlighting().any {
            it.severity == HighlightSeverity.ERROR &&
            it.description?.contains("Unknown constant", ignoreCase = true) == true
        }
    }

    fun testSimpleDefineNoAnnotatorError() {
        assertFalse(hasUnknownConstantError(
            "#define AppName MyApp\n[Files]\nSource: \"a.exe\"; DestDir: \"{#AppName}\"\n"))
    }

    fun testMacroNoAnnotatorError() {
        assertFalse(hasUnknownConstantError(
            "#define Max(a,b) body\n[Files]\nSource: \"a.exe\"; DestDir: \"{#Max}\"\n"))
    }

    fun testTypedIntDefineNoAnnotatorError() {
        assertFalse(hasUnknownConstantError(
            "#define int MyInt 42\n[Files]\nSource: \"a.exe\"; DestDir: \"{#MyInt}\"\n"))
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 6.  Completion — all define names appear in {#} popup
    // ═══════════════════════════════════════════════════════════════════════════

    fun testSimpleDefineAppearsInCompletion() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#<caret>}\"\n")
        myFixture.completeBasic()
        assertTrue("Simple define must appear in {#} completion",
            "AppVersion" in (myFixture.lookupElementStrings ?: emptyList()))
    }

    fun testMacroAppearsInCompletion() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define Max(a,b) body\n#define Min(a,b) body\n[Files]\nSource: \"a.exe\"; DestDir: \"{#<caret>}\"\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertTrue("Macro name must appear in {#} completion", "Max" in variants)
        assertTrue("Macro name must appear in {#} completion", "Min" in variants)
    }

    fun testTypedDefineAppearsInCompletion() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define int MyVersion 1\n#define str MyTitle \"T\"\n[Files]\nSource: \"a.exe\"; DestDir: \"{#<caret>}\"\n")
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertTrue("Typed int define must appear in {#} completion", "MyVersion" in variants)
        assertTrue("Typed str define must appear in {#} completion", "MyTitle" in variants)
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 7.  Rename — declaration and all {#Name} usages updated
    // ═══════════════════════════════════════════════════════════════════════════

    fun testRenameSimpleDefine() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define App<caret>Name \"MyApp\"\n[Setup]\nAppName={#AppName}\n")
        myFixture.renameElementAtCaret("ProductName")
        myFixture.checkResult(
            "#define ProductName \"MyApp\"\n[Setup]\nAppName={#ProductName}\n")
    }

    fun testRenameMacroUpdatesDeclarationAndReferences() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define Ma<caret>x(a,b) ((a)>(b)?(a):(b))\n[Files]\nSource: \"a.exe\"; DestDir: \"{#Max}\"\n")
        myFixture.renameElementAtCaret("Biggest")
        myFixture.checkResult(
            "#define Biggest(a,b) ((a)>(b)?(a):(b))\n[Files]\nSource: \"a.exe\"; DestDir: \"{#Biggest}\"\n")
    }

    fun testRenameTypedIntDefine() {
        myFixture.configureByText(IssFileType.INSTANCE,
            "#define int MyI<caret>nt 42\n[Setup]\nAppVersion={#MyInt}\n")
        myFixture.renameElementAtCaret("MyNumber")
        myFixture.checkResult(
            "#define int MyNumber 42\n[Setup]\nAppVersion={#MyNumber}\n")
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // 8.  Annotator — type validation for typed defines
    // ═══════════════════════════════════════════════════════════════════════════

    private fun hasTypeError(content: String): Boolean {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        return myFixture.doHighlighting().any {
            it.severity == HighlightSeverity.ERROR &&
            it.description?.contains("requires", ignoreCase = true) == true
        }
    }

    fun testTypedIntValidValueNoError() {
        assertFalse("Valid integer value must produce no type error",
            hasTypeError("#define int MyConst 42\n"))
    }

    fun testTypedIntNegativeValueNoError() {
        assertFalse("Negative integer must produce no type error",
            hasTypeError("#define int MyConst -7\n"))
    }

    fun testTypedIntInvalidValueProducesError() {
        assertTrue("Non-integer value for 'int' define must produce type ERROR",
            hasTypeError("#define int MyConst hello\n"))
    }

    fun testTypedIntFloatValueProducesError() {
        assertTrue("Float value for 'int' define must produce type ERROR",
            hasTypeError("#define int MyConst 3.14\n"))
    }

    fun testTypedFloatValidValueNoError() {
        assertFalse("Valid float value must produce no type error",
            hasTypeError("#define float MyConst 3.14\n"))
    }

    fun testTypedFloatIntegerValueNoError() {
        assertFalse("Integer is also a valid float",
            hasTypeError("#define float MyConst 42\n"))
    }

    fun testTypedFloatInvalidValueProducesError() {
        assertTrue("Non-numeric value for 'float' define must produce type ERROR",
            hasTypeError("#define float MyConst hello\n"))
    }

    fun testTypedStrAnyValueNoError() {
        assertFalse("String type accepts any value",
            hasTypeError("#define str MyConst anything goes here\n"))
    }

    fun testTypedStrNoValueNoError() {
        assertFalse("Typed define without a value must not produce type error",
            hasTypeError("#define int MyConst\n"))
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.lang.injection.InjectedLanguageManager
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

class IsPreprocessorDocumentationProviderTest : IsTimedBasePlatformTestCase() {

    private val provider = IsPreprocessorDocumentationProvider()

    private fun docFor(content: String): String? {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        val offset = myFixture.caretOffset
        // The directive lives in the injected ISPP file, not the ISS host file.
        val ctx = InjectedLanguageManager.getInstance(project).findInjectedElementAt(myFixture.file, offset)
            ?: myFixture.file.findElementAt(offset)
            ?: return null
        val target = provider.getCustomDocumentationElement(
            myFixture.editor, ctx.containingFile, ctx, offset
        ) ?: ctx
        return provider.generateDoc(target, ctx)
    }

    /** Mirrors the completion-popup path for the injected ISPP fragment. */
    private fun lookupDocFor(lookup: String, content: String): String? {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        val offset = myFixture.caretOffset
        val ctx = InjectedLanguageManager.getInstance(project).findInjectedElementAt(myFixture.file, offset)
            ?: myFixture.file.findElementAt(offset)
            ?: return null
        val target = provider.getDocumentationElementForLookupItem(ctx.manager, lookup, ctx)
            ?: return null
        return provider.generateDoc(target, ctx)
    }

    fun testLookupDirectiveKeywordDoc() {
        val doc = lookupDocFor("define", "#def<caret>ine\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected lookup doc for the #define directive keyword", doc)
        assertTrue("Doc must name the directive", doc!!.contains("#define"))
        assertTrue("Doc must declare it is a directive", doc.contains("directive"))
    }

    fun testLookupPredefinedVariableDoc() {
        val doc = lookupDocFor("PREPROCVER", "#define A PREPROC<caret>VER\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected lookup doc for the PREPROCVER predefined variable", doc)
        assertTrue("Doc must name the variable", doc!!.contains("PREPROCVER"))
    }

    fun testLookupUserDefineDoc() {
        val doc = lookupDocFor(
            "First",
            "#define First 10\n#define Second Firs<caret>t\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertNotNull("Expected lookup doc for the referenced own #define 'First'", doc)
        assertTrue("Doc must name the define", doc!!.contains("First"))
        assertTrue("Doc must show the computed value 10", doc.contains("10"))
    }

    fun testDirectiveKeywordDoc() {
        val doc = docFor("#def<caret>ine MyConst \"1.0\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected doc for the #define directive keyword", doc)
        assertTrue("Doc must name the directive", doc!!.contains("#define"))
        assertTrue("Doc must declare it is a directive", doc.contains("directive"))
        assertTrue("Doc must contain the directive description", doc.contains("Defines a macro variable"))
        assertTrue("Doc must show the syntax", doc.contains("Syntax"))
    }

    fun testIncludeDirectiveDoc() {
        val doc = docFor("#inc<caret>lude \"common.iss\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected doc for the #include directive", doc)
        assertTrue("Doc must name the include directive", doc!!.contains("#include"))
    }

    fun testPredefinedVariableDoc() {
        val doc = docFor("#define A PREPROC<caret>VER\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected doc for the PREPROCVER predefined variable", doc)
        assertTrue("Doc must name the variable", doc!!.contains("PREPROCVER"))
        assertTrue("Doc must contain the variable description", doc.contains("version", ignoreCase = true))
    }

    fun testLookupBuiltinFunctionDoc() {
        val doc = lookupDocFor("FileExists", "#define A FileExi<caret>sts\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected lookup doc for the FileExists built-in function", doc)
        assertTrue("Doc must name the function", doc!!.contains("FileExists"))
        assertTrue("Doc must declare it is a function", doc.contains("function"))
        assertTrue("Doc must show the signature", doc.contains("Signature"))
    }

    fun testBuiltinFunctionDoc() {
        val doc = docFor("#define A FileExi<caret>sts\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected doc for the FileExists built-in function", doc)
        assertTrue("Doc must name the function", doc!!.contains("FileExists"))
        assertTrue("Doc must show the signature", doc.contains("Signature"))
    }

    fun testSimpleDefineOwnNameDoc() {
        val doc = docFor("#define MyCon<caret>st \"1.0\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected doc for the own #define name", doc)
        assertTrue("Doc must name the define", doc!!.contains("MyConst"))
        assertTrue("Doc must show the str type", doc.contains("str"))
        assertTrue("Doc must show the computed value", doc.contains("\"1.0\""))
    }

    fun testReferencedSimpleDefineShowsTypeAndComputedValue() {
        // Hovering the 'First' reference inside Second's expression documents First (int, value 10).
        val doc = docFor(
            "#define First 10\n#define Second Firs<caret>t + 5\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertNotNull("Expected doc for the referenced define 'First'", doc)
        assertTrue("Doc must name the define", doc!!.contains("First"))
        assertTrue("Doc must show the int type", doc.contains("int"))
        assertTrue("Doc must show the computed value 10", doc.contains("10"))
    }

    fun testComputedValueAcrossReferences() {
        // Sum = Base + 5 = 15 — the computed value is resolved transitively.
        val doc = docFor(
            "#define Base 10\n#define Su<caret>m Base + 5\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertNotNull(doc)
        assertTrue("Computed value must be 15", doc!!.contains("15"))
    }

    fun testComputedValueThroughMacroCall() {
        // hello = demo + anyany("y") = "test" + ("demo.isl" + "y") = "testdemo.isly"
        val doc = docFor(
            "#define demo \"test\"\n#define lang \"demo.isl\"\n#define anyany(x) lang + x\n" +
                    "#define hel<caret>lo demo + anyany(\"y\")\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertNotNull(doc)
        assertTrue("Computed value must resolve through the macro call", doc!!.contains("testdemo.isly"))
    }

    fun testFunctionMacroDefineDocShowsParamsAndReturnType() {
        // func(x) "abc" + x → x is constrained to str, return type str.
        val doc = docFor("#define fun<caret>c(x) \"abc\" + x\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected doc for the function-like macro", doc)
        assertTrue("Doc must name the macro", doc!!.contains("func"))
        assertTrue("Doc must declare it is a macro", doc.contains("macro"))
        assertTrue("Doc must show the parameter type", doc.contains("x: str"))
        assertTrue("Doc must show the return type str", doc.contains("· str") || doc.contains("str"))
    }

    // ── Built-in function details from the specification ─────────────────────

    /**
     * Quick Doc must also work at a real call site (the function name directly followed by `(`), not only
     * on a bare identifier — that is where the user actually asks for it.
     */
    fun testBuiltinFunctionDocAtCallSite() {
        val doc = docFor("#define A Co<caret>py(\"abc\", 1, 2)\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull("Expected doc for the Copy built-in function at its call site", doc)
        assertTrue("Doc must name the function", doc!!.contains("Copy"))
        assertTrue("Doc must show the signature", doc.contains("Signature"))
    }

    /**
     * The parameters of a built-in are listed individually with their declared type, taken from the parsed
     * signature of `is-preprocessor.yaml`.
     */
    fun testBuiltinFunctionDocListsParameters() {
        val doc = docFor("#define A Co<caret>py(\"abc\", 1, 2)\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull(doc)
        assertTrue("Doc must have a parameter section", doc!!.contains("Parameters"))
        assertTrue("Doc must list the string parameter", doc.contains("S: str"))
        assertTrue("Doc must list the integer parameters", doc.contains("Index: int"))
    }

    /** A parameter with a default value is marked as optional and shows that default. */
    fun testBuiltinFunctionDocShowsOptionalParameterDefault() {
        val doc = docFor("#define A Fi<caret>nd(\"abc\", \"b\")\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull(doc)
        assertTrue("Doc must mark the optional parameter", doc!!.contains("optional"))
    }

    /** A by-reference parameter (`str*`) is called out as such. */
    fun testBuiltinFunctionDocShowsByReferenceParameter() {
        val doc = docFor(
            "#define A StringCh<caret>ange(MyVar, \"a\", \"b\")\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertNotNull(doc)
        assertTrue("Doc must mark the by-reference parameter", doc!!.contains("by reference"))
    }

    /** An `Ident` parameter is explained as an un-evaluated symbol name that may be undefined. */
    fun testBuiltinFunctionDocExplainsSymbolParameter() {
        val doc = docFor("#define A Defi<caret>ned(DEBUG)\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull(doc)
        assertTrue("Doc must explain the symbol parameter", doc!!.contains("symbol name"))
    }

    /** A function marked `pure: false` in the specification carries a side-effect note. */
    fun testImpureBuiltinFunctionDocShowsSideEffectNote() {
        val doc = docFor("#define A FileExi<caret>sts(\"a.txt\")\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull(doc)
        assertTrue("Doc of an impure function must note the side effects", doc!!.contains("side effects"))
    }

    /** A pure function must not carry the side-effect note. */
    fun testPureBuiltinFunctionDocHasNoSideEffectNote() {
        val doc = docFor("#define A Co<caret>py(\"abc\", 1, 2)\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNotNull(doc)
        assertFalse("Doc of a pure function must not note side effects", doc!!.contains("side effects"))
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

class IsPreprocessorDocumentationProviderTest : BasePlatformTestCase() {

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

    fun testLookupUserDefineProducesNoDoc() {
        val doc = lookupDocFor("MyConst", "#define A My<caret>\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNull("A user-defined macro name must not produce ISPP documentation", doc)
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

    fun testUserDefinedNameProducesNoDoc() {
        // The macro name itself is neither a directive keyword nor a predefined variable.
        val doc = docFor("#define MyCon<caret>st \"1.0\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n")
        assertNull("A user-defined macro name must not produce ISPP documentation", doc)
    }
}

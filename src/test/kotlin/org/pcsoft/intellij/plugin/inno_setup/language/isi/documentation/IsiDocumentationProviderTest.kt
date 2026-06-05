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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.documentation

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation.IsiDocumentationProvider

class IsiDocumentationProviderTest : BasePlatformTestCase() {

    private val provider = IsiDocumentationProvider()

    private fun docFor(content: String): String? {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        val ctx = myFixture.file.findElementAt(myFixture.caretOffset)
        val target = provider.getCustomDocumentationElement(
            myFixture.editor, myFixture.file, ctx, myFixture.caretOffset
        ) ?: ctx ?: return null
        return provider.generateDoc(target, ctx)
    }

    fun testSectionNameDoc() {
        val doc = docFor("[Set<caret>up]\nAppName=My App\n")
        assertNotNull("Expected doc for section name", doc)
        assertTrue(doc!!.contains("Setup"))
    }

    fun testDirectiveKeyDoc() {
        val doc = docFor("[Setup]\nAppNa<caret>me=My App\n")
        assertNotNull("Expected doc for directive key", doc)
        assertTrue(doc!!.contains("AppName"))
    }

//    fun testParamKeyDoc() {
//        val doc = docFor("[Files]\nSourc<caret>e: \"app.exe\"; DestDir: \"{app}\"\n")
//        assertNotNull("Expected doc for param key", doc)
//        assertTrue(doc!!.contains("Source"))
//    }
//
//    fun testConstantDoc() {
//        val doc = docFor("[Files]\nSource: \"app.exe\"; DestDir: \"{a<caret>pp}\"\n")
//        assertNotNull("Expected doc for constant", doc)
//        assertTrue(doc!!.contains("app"))
//    }
//
//    fun testFlagDoc() {
//        val doc = docFor("[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"; Flags: ignoreversi<caret>on\n")
//        assertNotNull("Expected doc for flag", doc)
//        assertTrue(doc!!.contains("ignoreversion"))
//    }

    fun testValueContextReturnsNull() {
        val doc = docFor("[Setup]\nAppName=MyA<caret>pp\n")
        assertNull("Value text should not produce docs", doc)
    }
}

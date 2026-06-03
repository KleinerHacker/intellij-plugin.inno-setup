package org.pcsoft.intellij.plugin.inno_setup.language.documentation

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IssDocumentationProviderTest : BasePlatformTestCase() {

    private val provider = IssDocumentationProvider()

    private fun docFor(content: String): String? {
        myFixture.configureByText(IssFileType.INSTANCE, content)
        val ctx    = myFixture.file.findElementAt(myFixture.caretOffset)
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

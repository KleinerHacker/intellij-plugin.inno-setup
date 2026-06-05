package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IsiAnnotatorIsppTest : BasePlatformTestCase() {

    fun testKnownIsppConstantProducesNoError() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        )
        val highlights = myFixture.doHighlighting()
        val errors = highlights.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }
        assertTrue(
            "Known ISPP constant {#AppVersion} should produce no 'Unknown constant' error",
            errors.isEmpty()
        )
    }

    fun testUnknownIsppConstantProducesError() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "[Files]\nSource: \"app.exe\"; DestDir: \"{#Unknown}\"\n"
        )
        val highlights = myFixture.doHighlighting()
        val errors = highlights.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }
        assertTrue("Unknown ISPP constant {#Unknown} should produce an error", errors.isNotEmpty())
    }
}

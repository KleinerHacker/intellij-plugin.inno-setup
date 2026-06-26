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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.template

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.template.psi.IsTemplatePreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * A `.ist` template is free text: it must always parse without [PsiErrorElement]s, regardless of content,
 * while still recognizing preprocessor lines as the only structured element.
 */
class IsTemplateParserTest : IsTimedBasePlatformTestCase() {

    /** The host template file, even when a leading `#` line caused the ISPP injection to be returned. */
    private fun templateFile(): PsiFile {
        val raw = myFixture.file
        if (raw is IsTemplateFile) return raw
        return InjectedLanguageManager.getInstance(project).getTopLevelFile(raw)
    }

    private fun errors() =
        PsiTreeUtil.collectElementsOfType(templateFile(), PsiErrorElement::class.java)

    fun testPreprocessorLineRecognized() {
        myFixture.configureByText(IsTemplateFileType.INSTANCE, "#define X 1\n")
        val lines = PsiTreeUtil.collectElementsOfType(templateFile(), IsTemplatePreprocessorLine::class.java)
        assertEquals("Expected exactly one preprocessor line", 1, lines.size)
        assertTrue(errors().isEmpty())
    }

    fun testSectionHeaderProducesNoError() {
        myFixture.configureByText(IsTemplateFileType.INSTANCE, "[Setup]\n")
        assertTrue("A section header must parse without errors", errors().isEmpty())
    }

    fun testParensRecognizedWithoutError() {
        myFixture.configureByText(IsTemplateFileType.INSTANCE, "foo()\n")
        assertTrue(errors().isEmpty())
    }

    fun testArbitraryFreeTextHasNoErrors() {
        myFixture.configureByText(IsTemplateFileType.INSTANCE, "foo = bar; baz: qux {weird} <stuff>\n!!!???\n")
        assertTrue(
            "Free text must never produce parse errors, found:\n" +
                    errors().joinToString("\n") { "  at ${it.textOffset}: ${it.errorDescription}" },
            errors().isEmpty()
        )
    }

    fun testBrokenIssContentHasNoErrors() {
        myFixture.configureByText(
            IsTemplateFileType.INSTANCE,
            "[NoSuchSection]\nthis is not a valid key: : = value\nSource \"unterminated\n"
        )
        assertTrue("Broken .iss content in a template must not error", errors().isEmpty())
    }
}

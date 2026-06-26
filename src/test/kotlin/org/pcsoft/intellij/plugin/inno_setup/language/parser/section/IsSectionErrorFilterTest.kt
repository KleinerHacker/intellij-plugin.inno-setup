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
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests for [IsSectionErrorFilter] — parse errors inside the `\[Code]` section or an
 * ISPP preprocessor line must be suppressed, all others highlighted.
 *
 * An unclosed section header (`\[Name` without `]`) pins after `[` and yields a
 * [PsiErrorElement] inside that section, giving a deterministic in-section error.
 */
class IsSectionErrorFilterTest : IsTimedBasePlatformTestCase() {

    private val filter = IsSectionErrorFilter()

    private fun issFile(content: String): IsScriptFile {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return if (file is IsScriptFile) file
        else InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IsScriptFile
    }

    private fun errorsIn(content: String): List<PsiErrorElement> =
        PsiTreeUtil.collectElementsOfType(issFile(content), PsiErrorElement::class.java).toList()

    fun testErrorInCodeSectionIsSuppressed() {
        // Unclosed [Code header → error inside the [Code] section; no body lines so
        // no additional errors leak out to the file level.
        val errors = errorsIn("[Setup]\nAppName=x\nAppVersion=1\n\n[Code\n")
        assertTrue("Expected a parse error from the unclosed [Code header", errors.isNotEmpty())
        assertTrue(
            "Errors inside the [Code] section must be suppressed",
            errors.all { !filter.shouldHighlightErrorElement(it) })
    }

    fun testErrorInNormalSectionIsHighlighted() {
        val errors = errorsIn("[Setup]\nAppName=x\nAppVersion=1\n\n[Files\nSource: \"a.exe\"\n")
        assertTrue("Expected a parse error from the unclosed [Files header", errors.isNotEmpty())
        assertTrue(
            "Errors in a normal section must be highlighted",
            errors.all { filter.shouldHighlightErrorElement(it) })
    }
}

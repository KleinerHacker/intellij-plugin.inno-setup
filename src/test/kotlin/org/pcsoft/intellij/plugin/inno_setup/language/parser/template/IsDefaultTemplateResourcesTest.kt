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
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateFile
import java.io.File

/**
 * Validates every `*.ist` template under `src/test/resources/default`: each must parse without any
 * [PsiErrorElement]. New example templates dropped into that directory are picked up automatically.
 */
class IsDefaultTemplateResourcesTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String = TEST_DATA_PATH

    /** The host template file, even when a leading `#` line caused the ISPP injection to be returned. */
    private fun templateFile(raw: PsiFile): PsiFile {
        if (raw is IsTemplateFile) return raw
        return InjectedLanguageManager.getInstance(project).getTopLevelFile(raw)
    }

    fun testAllDefaultTemplatesHaveNoErrors() {
        val templates = File(TEST_DATA_PATH).walkTopDown().filter { it.isFile && it.extension == "ist" }.toList()
        assertTrue("No *.ist files found under $TEST_DATA_PATH", templates.isNotEmpty())

        templates.forEach { template ->
            val relative = template.relativeTo(File(TEST_DATA_PATH)).path.replace('\\', '/')
            val copied = myFixture.copyFileToProject(relative, relative)
            myFixture.configureFromExistingVirtualFile(copied)

            val errors = PsiTreeUtil.collectElementsOfType(templateFile(myFixture.file), PsiErrorElement::class.java)
            assertTrue(
                "$relative must not contain parse errors, but got:\n" +
                        errors.joinToString("\n") { "  at ${it.textOffset}: ${it.errorDescription}" },
                errors.isEmpty(),
            )
        }
    }

    private companion object {
        const val TEST_DATA_PATH = "src/test/resources/default"
    }
}

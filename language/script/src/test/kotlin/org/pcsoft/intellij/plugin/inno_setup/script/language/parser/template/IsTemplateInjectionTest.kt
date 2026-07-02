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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.template

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.template.IsTemplateFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.template.psi.IsTemplatePreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * The ISPP language must be injected into preprocessor lines of `.ist` template files, just like in `.iss`.
 */
class IsTemplateInjectionTest : IsTimedBasePlatformTestCase() {

    private fun isppFilesIn(text: String): List<IsPreprocessorFile> {
        myFixture.configureByText(IsTemplateFileType.INSTANCE, text)
        val top = InjectedLanguageManager.getInstance(project).getTopLevelFile(myFixture.file)
        val mgr = InjectedLanguageManager.getInstance(project)
        val result = mutableListOf<IsPreprocessorFile>()
        PsiTreeUtil.collectElementsOfType(top, IsTemplatePreprocessorLine::class.java).forEach { line ->
            mgr.enumerate(line) { injected, _ ->
                if (injected is IsPreprocessorFile) result.add(injected)
            }
        }
        return result
    }

    fun testDefineLineGetsIsppInjected() {
        val injected = isppFilesIn("#define MyVar 1\n")
        assertEquals("ISPP must be injected into the #define line", 1, injected.size)
    }

    fun testIncludeLineGetsIsppInjected() {
        val injected = isppFilesIn("#include \"other.ist\"\n")
        assertEquals("ISPP must be injected into the #include line", 1, injected.size)
    }
}

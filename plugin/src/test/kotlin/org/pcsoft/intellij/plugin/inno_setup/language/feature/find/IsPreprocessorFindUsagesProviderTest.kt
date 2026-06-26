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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.find

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for [IsPreprocessorFindUsagesProvider] — find-usages is only enabled for `#define`
 * directives, and the descriptive name/type strings come from the define.
 */
class IsPreprocessorFindUsagesProviderTest : IsTimedBasePlatformTestCase() {

    private val provider = IsPreprocessorFindUsagesProvider()

    private fun setup(content: String): IsScriptFile {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return if (file is IsScriptFile) file
        else InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IsScriptFile
    }

    /** All ISPP directives across injected preprocessor lines. */
    private fun directives(file: IsScriptFile): List<IsPreprocessorDirective> {
        val mgr = InjectedLanguageManager.getInstance(file.project)
        return PsiTreeUtil.getChildrenOfTypeAsList(file, IsSectionPreprocessorLine::class.java)
            .flatMap { line ->
                val dirs = mutableListOf<IsPreprocessorDirective>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsPreprocessorFile)
                        dirs.addAll(
                            PsiTreeUtil.getChildrenOfTypeAsList(
                                injectedPsi,
                                IsPreprocessorDirective::class.java
                            )
                        )
                }
                dirs
            }
    }

    fun testCanFindUsagesForDefine() {
        val file = setup("#define MyVar \"x\"\n#include \"other.iss\"\n")
        val define = directives(file).first { (it as IsPreprocessorDirectiveEx).isDefine() }
        assertTrue("find-usages must be enabled for #define", provider.canFindUsagesFor(define))
    }

    fun testCannotFindUsagesForNonDefineDirective() {
        val file = setup("#define MyVar \"x\"\n#include \"other.iss\"\n")
        val include = directives(file).first { !(it as IsPreprocessorDirectiveEx).isDefine() }
        assertFalse("find-usages must be disabled for #include", provider.canFindUsagesFor(include))
    }

    fun testCannotFindUsagesForUnrelatedElement() {
        val file = setup("#define MyVar \"x\"\n")
        assertFalse(
            "find-usages must be disabled for a non-directive element",
            provider.canFindUsagesFor(file)
        )
    }

    fun testDescriptiveNameAndType() {
        val file = setup("#define MyVar \"x\"\n")
        val define = directives(file).first { (it as IsPreprocessorDirectiveEx).isDefine() }
        assertEquals("ISPP define", provider.getType(define))
        assertEquals("MyVar", provider.getDescriptiveName(define))
        assertEquals(provider.getDescriptiveName(define), provider.getNodeText(define, true))
    }

    fun testHelpIdIsNullAndWordsScannerExists() {
        val file = setup("#define MyVar \"x\"\n")
        val define = directives(file).first { (it as IsPreprocessorDirectiveEx).isDefine() }
        assertNull(provider.getHelpId(define))
        assertNotNull(provider.getWordsScanner())
    }
}

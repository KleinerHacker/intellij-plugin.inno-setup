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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.editor.section.IsSectionCodeFoldingBuilder
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests folding of `#if … #endif` blocks: a fold is offered only when the whole range lies within one
 * section or entirely outside any section — never when it crosses a section header.
 */
class IsPreprocessorConditionalFoldingTest : IsTimedBasePlatformTestCase() {

    private fun conditionalFolds(content: String): List<FoldingDescriptor> {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        val hostFile = if (file is IsScriptFile) file
        else InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IsScriptFile
        val doc = PsiDocumentManager.getInstance(project).getDocument(hostFile)!!
        return IsSectionCodeFoldingBuilder().buildFoldRegions(hostFile, doc, false)
            // Keep only the ISPP conditional folds (anchored on a preprocessor line).
            .filter { it.element.psi is IsSectionPreprocessorLine }
    }

    private fun snippet(content: String, range: TextRange): String =
        content.substring(range.startOffset, range.endOffset)

    fun testFoldOutsideAnySection() {
        val content = "#if 1\n#define X 1\n#endif\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val folds = conditionalFolds(content)
        assertEquals("Expected one conditional fold", 1, folds.size)
        val text = snippet(content, folds[0].range)
        assertTrue("Fold must start at #if", text.startsWith("#if"))
        assertTrue("Fold must end at #endif", text.trimEnd().endsWith("#endif"))
    }

    fun testFoldInsideOneSection() {
        val content = "[Setup]\n#if 1\nAppName=Test\n#endif\nAppVersion=1.0\n"
        val folds = conditionalFolds(content)
        assertEquals("A block fully inside one section must fold", 1, folds.size)
    }

    fun testNoFoldWhenCrossingSectionHeader() {
        val content = "#if 1\n[Setup]\nAppName=Test\n#endif\nAppVersion=1.0\n"
        val folds = conditionalFolds(content)
        assertTrue("A block crossing a section header must not fold", folds.isEmpty())
    }

    fun testNestedBlocksProduceTwoFolds() {
        val content = "#if 1\n#if 2\n#define X 1\n#endif\n#endif\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val folds = conditionalFolds(content)
        assertEquals("Nested #if/#endif must each fold", 2, folds.size)
    }

    fun testUnterminatedIfDoesNotFold() {
        val content = "#if 1\n#define X 1\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        assertTrue("An unterminated #if must not fold", conditionalFolds(content).isEmpty())
    }
}

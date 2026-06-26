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
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests folding of `#sub … #endsub` blocks: behaves exactly like the `#if … #endif` fold — only offered when
 * the whole range lies within one section or entirely outside any section, never across a section header.
 */
class IsPreprocessorSubroutineFoldingTest : IsTimedBasePlatformTestCase() {

    private fun subroutineFolds(content: String): List<FoldingDescriptor> {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        val hostFile = if (file is IsScriptFile) file
        else InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as IsScriptFile
        val doc = PsiDocumentManager.getInstance(project).getDocument(hostFile)!!
        return IsSectionCodeFoldingBuilder().buildFoldRegions(hostFile, doc, false)
            .filter { it.element.psi is IsSectionPreprocessorLine }
    }

    private fun snippet(content: String, range: TextRange): String =
        content.substring(range.startOffset, range.endOffset)

    fun testFoldOutsideAnySection() {
        val content = "#sub Foo\n#define X 1\n#endsub\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val folds = subroutineFolds(content)
        assertEquals("Expected one subroutine fold", 1, folds.size)
        val text = snippet(content, folds[0].range)
        assertTrue("Fold must start at #sub", text.startsWith("#sub"))
        assertTrue("Fold must end at #endsub", text.trimEnd().endsWith("#endsub"))
    }

    fun testFoldInsideOneSection() {
        val content = "[Files]\n#sub Foo\nSource: a.txt; DestDir: {app}\n#endsub\n"
        assertEquals("A block fully inside one section must fold", 1, subroutineFolds(content).size)
    }

    fun testNoFoldWhenCrossingSectionHeader() {
        val content = "#sub Foo\n[Setup]\nAppName=Test\n#endsub\nAppVersion=1.0\n"
        assertTrue("A block crossing a section header must not fold", subroutineFolds(content).isEmpty())
    }

    fun testNestedBlocksProduceTwoFolds() {
        val content = "#sub Outer\n#sub Inner\n#define X 1\n#endsub\n#endsub\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        assertEquals("Nested #sub/#endsub must each fold", 2, subroutineFolds(content).size)
    }

    fun testUnterminatedSubDoesNotFold() {
        val content = "#sub Foo\n#define X 1\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        assertTrue("An unterminated #sub must not fold", subroutineFolds(content).isEmpty())
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.preprocessor

import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.IsSectionAnnotatorHighlighting
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Verifies that **every** occurrence of a `#for` loop variable — its declaration in the initializer and each
 * use in the condition/increment/body — is painted with the italic [IsSectionAnnotatorHighlighting.DEFINE_NAME]
 * colour, so the loop variable is consistently recognizable.
 */
class IsPreprocessorForLoopHighlightingTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun highlights(content: String) =
        myFixture.run {
            configureByText(IsScriptFileType.INSTANCE, content)
            doHighlighting()
        }

    fun testEveryLoopVariableOccurrenceIsDefineNameColored() {
        // '#for {i = 200; i > 0; i--} AddFile' with AddFile a #sub — there are three `i` occurrences.
        val text = "#sub AddFile\n#endsub\n#for {i = 200; i > 0; i--} AddFile\n$setupTail"
        val all = highlights(text)

        val forLine = text.indexOf("#for")
        val occurrences = listOf(
            text.indexOf("i = 200", forLine),       // declaration
            text.indexOf("i > 0", forLine),          // condition use
            text.indexOf("i--", forLine),            // increment use
        )

        occurrences.forEach { offset ->
            val hit = all.firstOrNull { info ->
                info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.DEFINE_NAME &&
                        info.startOffset == offset && info.endOffset == offset + 1
            }
            assertNotNull(
                "Loop variable 'i' at offset $offset must be painted DEFINE_NAME (italic); highlights=" +
                        all.filter { it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.DEFINE_NAME }
                            .map { it.startOffset to it.endOffset },
                hit,
            )
        }
    }
}

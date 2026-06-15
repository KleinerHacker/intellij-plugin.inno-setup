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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests that [IsPreprocessorAnnotator] validates the directive keyword against the ISPP spec
 * ([org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService], the single source of truth).
 */
class IsPreprocessorAnnotatorDirectiveTest : BasePlatformTestCase() {

    private fun unknownDirectiveErrors(text: String) =
        myFixture.also { it.configureByText(IsScriptFileType.INSTANCE, text) }
            .doHighlighting()
            .filter {
                it.severity.name == "ERROR" &&
                        it.description?.contains("Unknown preprocessor directive") == true
            }

    fun testKnownDirectiveProducesNoError() {
        val errors = unknownDirectiveErrors(
            "#define MyConst \"1.0\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Known directive '#define' must not be flagged", errors.isEmpty())
    }

    fun testKnownDirectiveIsCaseInsensitive() {
        // ISPP directives are case-insensitive.
        val errors = unknownDirectiveErrors(
            "#DEFINE MyConst \"1.0\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("'#DEFINE' must be accepted (directives are case-insensitive)", errors.isEmpty())
    }

    fun testUnknownDirectiveProducesError() {
        val errors = unknownDirectiveErrors(
            "#frobnicate something\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        assertTrue("Unknown directive '#frobnicate' must produce an error", errors.isNotEmpty())
        assertTrue(
            "Error message should name the offending directive",
            errors.any { it.description?.contains("#frobnicate") == true }
        )
    }
}

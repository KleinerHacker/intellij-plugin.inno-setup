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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Ground-truth tests for paired-brace auto-close **inside preprocessor directive lines**: typing an opening
 * `(`, `[` or `{` must insert the matching closing character.
 */
class IsPreprocessorDirectiveBraceTypingTest : IsTimedBasePlatformTestCase() {

    private val tail = "\n[Setup]\nAppName=Test\nAppVersion=1.0\n"

    fun testDefineFunctionMacroParenAutoCloses() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#define meth<caret>$tail")
        myFixture.type("(")
        myFixture.checkResult("#define meth(<caret>)$tail")
    }

    fun testDimArrayBracketAutoCloses() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#dim arr<caret>$tail")
        myFixture.type("[")
        myFixture.checkResult("#dim arr[<caret>]$tail")
    }

    fun testForBraceAutoCloses() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "#for <caret>$tail")
        myFixture.type("{")
        myFixture.checkResult("#for {<caret>}$tail")
    }
}

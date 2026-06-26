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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.template

import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.template.psi.IsTemplateTypes
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

class IsTemplateBraceMatcherTest : IsTimedBasePlatformTestCase() {

    fun testBracePairsCount() {
        assertEquals(2, IsTemplateBraceMatcher.PAIRS.size)
    }

    fun testSquareBracketPairIsStructural() {
        val pair = IsTemplateBraceMatcher.PAIRS.first { it.leftBraceType == IsTemplateTypes.LBRACKET }
        assertEquals(IsTemplateTypes.RBRACKET, pair.rightBraceType)
        assertTrue("[] must be structural", pair.isStructural)
    }

    fun testParenPairNotStructural() {
        val pair = IsTemplateBraceMatcher.PAIRS.first { it.leftBraceType == IsTemplateTypes.LPAREN }
        assertEquals(IsTemplateTypes.RPAREN, pair.rightBraceType)
        assertFalse("() must not be structural", pair.isStructural)
    }

    fun testSquareBracketAutoCloses() {
        myFixture.configureByText(IsTemplateFileType.INSTANCE, "<caret>")
        myFixture.type("[")
        myFixture.checkResult("[<caret>]")
    }

    fun testParenAutoCloses() {
        myFixture.configureByText(IsTemplateFileType.INSTANCE, "<caret>")
        myFixture.type("(")
        myFixture.checkResult("(<caret>)")
    }
}

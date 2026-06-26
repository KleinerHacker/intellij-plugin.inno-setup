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

import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorTypes
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

class IsPreprocessorBraceMatcherTest : IsTimedBasePlatformTestCase() {

    fun testBracePairsCount() {
        assertEquals(3, IsPreprocessorBraceMatcher.PAIRS.size)
    }

    fun testBracePair() {
        val pair = IsPreprocessorBraceMatcher.PAIRS.first { it.leftBraceType == IsPreprocessorTypes.LBRACE }
        assertEquals(IsPreprocessorTypes.RBRACE, pair.rightBraceType)
        assertFalse("{} must not be structural", pair.isStructural)
    }

    fun testParenPair() {
        val pair = IsPreprocessorBraceMatcher.PAIRS.first { it.leftBraceType == IsPreprocessorTypes.LPAREN }
        assertEquals(IsPreprocessorTypes.RPAREN, pair.rightBraceType)
        assertFalse("() must not be structural", pair.isStructural)
    }

    fun testBracketPair() {
        val pair = IsPreprocessorBraceMatcher.PAIRS.first { it.leftBraceType == IsPreprocessorTypes.LBRACKET }
        assertEquals(IsPreprocessorTypes.RBRACKET, pair.rightBraceType)
        assertFalse("[] must not be structural", pair.isStructural)
    }

    fun testIsPairedBracesAllowedBeforeTypeAlwaysTrue() {
        val matcher = IsPreprocessorBraceMatcher()
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IsPreprocessorTypes.LPAREN, null))
        assertTrue(matcher.isPairedBracesAllowedBeforeType(IsPreprocessorTypes.LPAREN, IsPreprocessorTypes.IDENTIFIER))
    }
}

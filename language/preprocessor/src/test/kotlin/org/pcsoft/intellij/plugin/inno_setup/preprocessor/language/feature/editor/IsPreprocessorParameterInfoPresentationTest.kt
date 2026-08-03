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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.editor

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Verifies [renderParameterInfo]: the label of the parameter info popup and the range highlighted for the
 * argument the caret currently sits in.
 */
class IsPreprocessorParameterInfoPresentationTest {

    private val noParameters = "<no parameters>"

    /** The parameters are joined with `, ` and the result type is appended. */
    @Test
    fun `parameters are joined and the result type appended`() {
        val presentation = renderParameterInfo(
            IsPreprocessorParameterInfoItem(listOf("S: str", "Index: int"), "str"),
            0,
            noParameters,
        )
        assertEquals("S: str, Index: int → str", presentation.text)
    }

    /** The current parameter is highlighted at its exact offsets inside the label. */
    @Test
    fun `current parameter is highlighted`() {
        val item = IsPreprocessorParameterInfoItem(listOf("S: str", "Index: int", "Count: int"), "str")

        val first = renderParameterInfo(item, 0, noParameters)
        assertEquals(0, first.highlightStart)
        assertEquals("S: str".length, first.highlightEnd)

        val second = renderParameterInfo(item, 1, noParameters)
        assertEquals("S: str, ".length, second.highlightStart)
        assertEquals("S: str, Index: int".length, second.highlightEnd)
    }

    /** An argument index beyond the declared parameters highlights nothing. */
    @Test
    fun `index out of range highlights nothing`() {
        val presentation = renderParameterInfo(
            IsPreprocessorParameterInfoItem(listOf("S: str"), "int"),
            5,
            noParameters,
        )
        assertEquals(-1, presentation.highlightStart)
        assertEquals(-1, presentation.highlightEnd)
    }

    /** A signature without parameters shows the localized placeholder plus the result type. */
    @Test
    fun `signature without parameters shows the placeholder`() {
        val presentation = renderParameterInfo(
            IsPreprocessorParameterInfoItem(emptyList(), "int"),
            0,
            noParameters,
        )
        assertEquals("<no parameters> → int", presentation.text)
        assertEquals(-1, presentation.highlightStart)
    }

    /** An undeterminable result type (an unconstrained macro) is simply omitted. */
    @Test
    fun `unknown result type is omitted`() {
        val presentation = renderParameterInfo(
            IsPreprocessorParameterInfoItem(listOf("a", "b"), null),
            1,
            noParameters,
        )
        assertEquals("a, b", presentation.text)
        assertEquals("a, ".length, presentation.highlightStart)
    }
}

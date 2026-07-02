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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression

/**
 * A statically computed value of an ISPP expression. Only integers and strings are representable; an
 * expression whose value cannot be determined (a function call, a `{…}` constant, an unresolved reference)
 * has no value and is represented as `null` by the evaluator rather than an instance of this type.
 */
sealed class IsPreprocessorExprValue {

    /** The ISPP type of this value. */
    abstract val type: IsPreprocessorExprType

    /** A human-readable rendering for documentation (strings are shown quoted). */
    abstract fun display(): String

    /** An integer value such as `15`. */
    data class IntValue(val value: Long) : IsPreprocessorExprValue() {
        override val type get() = IsPreprocessorExprType.INT
        override fun display() = value.toString()
    }

    /** A string value such as `"abc"`. */
    data class StrValue(val value: String) : IsPreprocessorExprValue() {
        override val type get() = IsPreprocessorExprType.STR
        override fun display() = "\"$value\""
    }
}

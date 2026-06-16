/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression

/**
 * The ISPP expression type system (`topic_expressions`).
 *
 * `VOID` is compatible with both [INT] and [STR] (treated as `0` resp. `""`). [ANY] is used whenever the
 * concrete type cannot be determined statically (e.g. an unresolved reference or a macro parameter); any
 * operation involving an [ANY] operand is considered valid to avoid false positives.
 */
enum class IsPreprocessorExprType {
    VOID,
    INT,
    STR,
    ANY;

    /** Compatible with an integer context (`INT`, `VOID` or `ANY`). */
    val intCompatible: Boolean get() = this == INT || this == VOID || this == ANY

    /** Compatible with a string context (`STR`, `VOID` or `ANY`). */
    val strCompatible: Boolean get() = this == STR || this == VOID || this == ANY
}

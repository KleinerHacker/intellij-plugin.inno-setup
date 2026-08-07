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

/** Category of an ISPP operator, used both for type rules and for highlighting. */
enum class IsPreprocessorExprOperatorCategory { ARITHMETIC, COMPARISON, LOGICAL, BITWISE, TERNARY, COMMA, ASSIGNMENT }

/**
 * ISPP binary operators with their C/C++-like precedence (higher binds tighter) and category.
 *
 * The preprocessor uses C/C++-like expression syntax; the precedence levels mirror that grammar so the
 * parser can use precedence climbing.
 */
enum class IsPreprocessorExprBinaryOperator(
    val symbol: String,
    val precedence: Int,
    val category: IsPreprocessorExprOperatorCategory,
) {
    COMMA(",", 1, IsPreprocessorExprOperatorCategory.COMMA),

    // ISPP expressions may assign — `#define M(str S) Local[0] = f(S), g(Local[0])` is the documented way
    // of holding an intermediate result inside a macro. As in C, assignment binds tighter than the comma
    // operator and looser than everything else.
    ASSIGN("=", 2, IsPreprocessorExprOperatorCategory.ASSIGNMENT),
    LOGICAL_OR("||", 3, IsPreprocessorExprOperatorCategory.LOGICAL),
    LOGICAL_AND("&&", 4, IsPreprocessorExprOperatorCategory.LOGICAL),
    BIT_OR("|", 5, IsPreprocessorExprOperatorCategory.BITWISE),
    BIT_XOR("^", 6, IsPreprocessorExprOperatorCategory.BITWISE),
    BIT_AND("&", 7, IsPreprocessorExprOperatorCategory.BITWISE),
    EQ("==", 8, IsPreprocessorExprOperatorCategory.COMPARISON),
    NEQ("!=", 8, IsPreprocessorExprOperatorCategory.COMPARISON),
    LT("<", 9, IsPreprocessorExprOperatorCategory.COMPARISON),
    GT(">", 9, IsPreprocessorExprOperatorCategory.COMPARISON),
    LE("<=", 9, IsPreprocessorExprOperatorCategory.COMPARISON),
    GE(">=", 9, IsPreprocessorExprOperatorCategory.COMPARISON),
    SHL("<<", 10, IsPreprocessorExprOperatorCategory.BITWISE),
    SHR(">>", 10, IsPreprocessorExprOperatorCategory.BITWISE),
    PLUS("+", 11, IsPreprocessorExprOperatorCategory.ARITHMETIC),
    MINUS("-", 11, IsPreprocessorExprOperatorCategory.ARITHMETIC),
    MUL("*", 12, IsPreprocessorExprOperatorCategory.ARITHMETIC),
    DIV("/", 12, IsPreprocessorExprOperatorCategory.ARITHMETIC),
    MOD("%", 12, IsPreprocessorExprOperatorCategory.ARITHMETIC);

    companion object {
        /** Lowest precedence among real binary operators (the comma operator). */
        const val MIN_PRECEDENCE = 1

        /** Precedence of the ternary `?:` operator (just above the comma operator). */
        const val TERNARY_PRECEDENCE = 2

        private val bySymbol = entries.associateBy { it.symbol }

        /** Binary operator for [symbol], or `null` if the symbol is not a binary operator. */
        fun forSymbol(symbol: String): IsPreprocessorExprBinaryOperator? = bySymbol[symbol]
    }
}

/** ISPP prefix (unary) operators. */
enum class IsPreprocessorExprUnaryOperator(val symbol: String) {
    PLUS("+"),
    MINUS("-"),
    NOT("!"),
    BIT_NOT("~");

    companion object {
        private val bySymbol = entries.associateBy { it.symbol }

        /** Unary operator for [symbol], or `null` if the symbol cannot be used as a prefix operator. */
        fun forSymbol(symbol: String): IsPreprocessorExprUnaryOperator? = bySymbol[symbol]
    }
}

/** Registry of the operator symbols the tokenizer recognises. */
object IsPreprocessorExprOperators {
    /**
     * Every operator symbol the tokenizer recognises, longest first so that multi-character operators
     * (`<<`, `==`, `&&`, …) win over their single-character prefixes.
     */
    val SYMBOLS: List<String> = listOf(
        "<<", ">>", "<=", ">=", "==", "!=", "&&", "||",
        // "=" MUST stay behind "==" and "!=" / "<=" / ">=" above, or the assignment operator would swallow
        // the first character of every comparison.
        "+", "-", "*", "/", "%", "&", "|", "^", "~", "!", "<", ">", "=",
    )
}

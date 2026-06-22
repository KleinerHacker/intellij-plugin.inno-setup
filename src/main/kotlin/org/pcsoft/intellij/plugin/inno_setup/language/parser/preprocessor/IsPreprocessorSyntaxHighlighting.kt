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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

/**
 * Holds editor highlighting attributes for Inno Setup inspections.
 */
object IsPreprocessorSyntaxHighlighting {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val STRING: TextAttributesKey = DefaultLanguageHighlighterColors.STRING
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val NUMBER: TextAttributesKey = DefaultLanguageHighlighterColors.NUMBER

    /**
     * Highlighting for ISPP expression operators (`+ - * / % & | ^ ~ << >> < > <= >= == != && || ! ? :`)
     * and grouping parentheses inside `#define` expressions.
     */
    val OPERATOR: TextAttributesKey = DefaultLanguageHighlighterColors.OPERATION_SIGN
}

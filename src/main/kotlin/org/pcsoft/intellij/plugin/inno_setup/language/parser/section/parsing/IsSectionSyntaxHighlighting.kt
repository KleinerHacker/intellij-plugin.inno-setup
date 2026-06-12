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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object IsSectionSyntaxHighlighting {
    val COMMENT: TextAttributesKey = DefaultLanguageHighlighterColors.LINE_COMMENT
    val STRING: TextAttributesKey = DefaultLanguageHighlighterColors.STRING
    val NUMBER: TextAttributesKey = DefaultLanguageHighlighterColors.NUMBER
    val BRACKET: TextAttributesKey = DefaultLanguageHighlighterColors.BRACKETS
    val BRACE: TextAttributesKey = DefaultLanguageHighlighterColors.BRACES
    val OPERATION_SIGN: TextAttributesKey = DefaultLanguageHighlighterColors.OPERATION_SIGN
    val KEYWORD: TextAttributesKey = DefaultLanguageHighlighterColors.KEYWORD
}

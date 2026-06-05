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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

object IsiAnnotatorHighlighting {
    val SECTION_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_SECTION_NAME",
        TextAttributes(JBColor(Color(0x000000), Color(0xA9B7C6)), null, null, null, Font.PLAIN)
    )
    val PARAM_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_PARAM_KEY",
        TextAttributes(JBColor(Color(0x660E7A), Color(0x9876AA)), null, null, null, Font.PLAIN)
    )
    val REFERENCE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_REFERENCE",
        TextAttributes(JBColor(Color(0x000000), Color(0xA9B7C6)), null, null, null, Font.PLAIN)
    )
    val FLAG: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_FLAG",
        TextAttributes(JBColor(Color(0x660E7A), Color(0x9876AA)), null, null, null, Font.ITALIC)
    )

    val PREPROCESSOR_DIRECTIVE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_PREPROCESSOR_DIRECTIVE",
        TextAttributes(JBColor(Color(0x0000C0), Color(0x56A8F5)), null, null, null, Font.PLAIN)
    )
    val ISPP_REFERENCE_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_ISPP_REFERENCE_NAME",
        TextAttributes(JBColor(Color(0x0000C0), Color(0x56A8F5)), null, null, null, Font.ITALIC)
    )
    val DEFINE_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_DEFINE_NAME",
        TextAttributes(JBColor(Color(0x000000), Color(0xA9B7C6)), null, null, null, Font.ITALIC)
    )

    val UNKNOWN_REFERENCE: TextAttributesKey = CodeInsightColors.WRONG_REFERENCES_ATTRIBUTES
    val DEPRECATED: TextAttributesKey = CodeInsightColors.DEPRECATED_ATTRIBUTES
}

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

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Font

object IsiSyntaxHighlighting {
    val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_COMMENT",
        TextAttributes(JBColor(Color(0x808080), Color(0x808080)), null, null, null, Font.ITALIC)
    )
    val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_STRING",
        TextAttributes(JBColor(Color(0x067D17), Color(0x6A8759)), null, null, null, Font.PLAIN)
    )
    val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_NUMBER",
        TextAttributes(JBColor(Color(0x1750EB), Color(0x6897BB)), null, null, null, Font.PLAIN)
    )
    val BRACKET: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_BRACKET",
        TextAttributes(JBColor(Color(0x000000), Color(0xA9B7C6)), null, null, null, Font.PLAIN)
    )
    val BRACE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_BRACE",
        TextAttributes(JBColor(Color(0x000000), Color(0xA9B7C6)), null, null, null, Font.PLAIN)
    )
    val OPERATION_SIGN: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_OPERATION_SIGN",
        TextAttributes(JBColor(Color(0x000000), Color(0xA9B7C6)), null, null, null, Font.PLAIN)
    )
    val KEYWORD: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "ISS_KEYWORD",
        TextAttributes(JBColor(Color(0x000080), Color(0xCC7832)), null, null, null, Font.BOLD)
    )
}

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

package org.pcsoft.intellij.plugin.inno_setup.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * A curated set of common Windows language identifiers (MSDN/[MS-LCID]) offered as completion for the
 * `[LangOptions]` `LanguageID` directive. [id] is the Pascal-hex LCID inserted into the script,
 * [displayName] is the locale shown to the user, and [flag] is the base name of an SVG under
 * `/icons/flags/` — or `null`, in which case a neutral globe icon is used.
 */
data class IssWindowsLanguage(val displayName: String, val id: String, private val flag: String?) {

    /** Flag icon for this locale, or a globe fallback when no flag is available. */
    val icon: Icon
        get() = flag?.let { IconLoader.getIcon("/icons/flags/$it.svg", IssWindowsLanguage::class.java) }
            ?: AllIcons.General.Web

    companion object {
        val entries: List<IssWindowsLanguage> = listOf(
            IssWindowsLanguage("English (United States)", "\$0409", "united-states"),
            IssWindowsLanguage("English (United Kingdom)", "\$0809", "english"),
            IssWindowsLanguage("German (Germany)", "\$0407", "german"),
            IssWindowsLanguage("French (France)", "\$040C", "french"),
            IssWindowsLanguage("Italian (Italy)", "\$0410", "italian"),
            IssWindowsLanguage("Spanish (Spain)", "\$0C0A", "spanish"),
            IssWindowsLanguage("Portuguese (Portugal)", "\$0816", "portuguese"),
            IssWindowsLanguage("Portuguese (Brazil)", "\$0416", "brazilianportuguese"),
            IssWindowsLanguage("Dutch (Netherlands)", "\$0413", "dutch"),
            IssWindowsLanguage("Russian (Russia)", "\$0419", "russian"),
            IssWindowsLanguage("Polish (Poland)", "\$0415", "polish"),
            IssWindowsLanguage("Czech (Czech Republic)", "\$0405", "czech"),
            IssWindowsLanguage("Danish (Denmark)", "\$0406", "danish"),
            IssWindowsLanguage("Finnish (Finland)", "\$040B", "finnish"),
            IssWindowsLanguage("Swedish (Sweden)", "\$041D", "sweden"),
            IssWindowsLanguage("Norwegian (Bokmål, Norway)", "\$0414", "norwegian"),
            IssWindowsLanguage("Greek (Greece)", "\$0408", "greek"),
            IssWindowsLanguage("Hungarian (Hungary)", "\$040E", "hungarian"),
            IssWindowsLanguage("Icelandic (Iceland)", "\$040F", "icelandic"),
            IssWindowsLanguage("Turkish (Turkey)", "\$041F", "turkish"),
            IssWindowsLanguage("Ukrainian (Ukraine)", "\$0422", "ukrainian"),
            IssWindowsLanguage("Serbian (Latin, Serbia)", "\$241A", "serbian"),
            IssWindowsLanguage("Slovenian (Slovenia)", "\$0424", "slovenian"),
            IssWindowsLanguage("Catalan (Catalan)", "\$0403", "catalan"),
            IssWindowsLanguage("Hebrew (Israel)", "\$040D", "hebrew"),
            IssWindowsLanguage("Japanese (Japan)", "\$0411", "japanese"),
            IssWindowsLanguage("Korean (Korea)", "\$0412", "korean"),
            IssWindowsLanguage("Chinese (Simplified, PRC)", "\$0804", "china"),
            IssWindowsLanguage("Chinese (Traditional, Taiwan)", "\$0404", "taiwan"),
            // Common locales without a dedicated flag icon — rendered with the globe fallback.
            IssWindowsLanguage("Arabic (Saudi Arabia)", "\$0401", null),
            IssWindowsLanguage("Bulgarian (Bulgaria)", "\$0402", null),
            IssWindowsLanguage("Croatian (Croatia)", "\$041A", null),
            IssWindowsLanguage("Romanian (Romania)", "\$0418", null),
            IssWindowsLanguage("Slovak (Slovakia)", "\$041B", null),
            IssWindowsLanguage("Thai (Thailand)", "\$041E", null),
            IssWindowsLanguage("Indonesian (Indonesia)", "\$0421", null),
            IssWindowsLanguage("Vietnamese (Vietnam)", "\$042A", null),
        )

        /**
         * Complete set of recognised Windows locale identifiers (numeric LCID values from the
         * MSDN [MS-LCID] table), used to validate the `LanguageID` directive. Far larger than the
         * curated completion [entries] so that valid but uncommon locales are not flagged.
         */
        val validIds: Set<Int> = setOf(
            0x0436, 0x041C, 0x0484, 0x045E,
            0x0401, 0x1401, 0x3C01, 0x0C01, 0x0801, 0x2C01, 0x3401, 0x3001, 0x1001, 0x1801,
            0x2001, 0x4001, 0x2801, 0x1C01, 0x3801, 0x2401,
            0x042B, 0x044D, 0x082C, 0x042C, 0x046D, 0x042D, 0x0423,
            0x0445, 0x0845, 0x201A, 0x141A, 0x047E, 0x0402, 0x0455, 0x0403,
            0x0C04, 0x1404, 0x0804, 0x1004, 0x0404, 0x0483, 0x041A, 0x101A,
            0x0405, 0x0406, 0x048C, 0x0465, 0x0813, 0x0413,
            0x0C09, 0x2809, 0x1009, 0x2409, 0x4009, 0x1809, 0x2009, 0x4409, 0x1409, 0x3409,
            0x4809, 0x1C09, 0x2C09, 0x0809, 0x0409, 0x3009,
            0x0425, 0x0438, 0x0464, 0x040B,
            0x080C, 0x0C0C, 0x040C, 0x140C, 0x180C, 0x100C,
            0x0462, 0x0456, 0x0437,
            0x0C07, 0x0407, 0x1407, 0x1007, 0x0807, 0x0408, 0x046F, 0x0447,
            0x0468, 0x040D, 0x0439, 0x040E, 0x040F, 0x0470, 0x0421,
            0x045D, 0x085D, 0x083C, 0x0410, 0x0810, 0x0411, 0x044B, 0x043F,
            0x0453, 0x0486, 0x0487, 0x0441, 0x0457, 0x0412, 0x0440, 0x0454,
            0x0426, 0x0427, 0x082E, 0x046E, 0x042F,
            0x083E, 0x043E, 0x044C, 0x043A, 0x0481, 0x047A, 0x044E, 0x047C,
            0x0450, 0x0850, 0x0461, 0x0414, 0x0814, 0x0482, 0x0448, 0x0463,
            0x0429, 0x0415, 0x0416, 0x0816, 0x0446,
            0x046B, 0x086B, 0x0C6B, 0x0418, 0x0417, 0x0419,
            0x243B, 0x103B, 0x143B, 0x0C3B, 0x043B, 0x083B, 0x203B, 0x183B, 0x1C3B,
            0x044F, 0x1C1A, 0x301A, 0x281A, 0x0C1A, 0x181A, 0x2C1A, 0x241A, 0x081A,
            0x046C, 0x0432, 0x045B, 0x041B, 0x0424,
            0x2C0A, 0x400A, 0x340A, 0x240A, 0x140A, 0x1C0A, 0x300A, 0x440A, 0x100A, 0x480A,
            0x080A, 0x4C0A, 0x180A, 0x3C0A, 0x280A, 0x500A, 0x0C0A, 0x040A, 0x540A, 0x380A, 0x200A,
            0x081D, 0x041D, 0x045A, 0x0428, 0x045F, 0x0449, 0x0444, 0x044A,
            0x041E, 0x0451, 0x041F, 0x0442, 0x0422, 0x042E, 0x0420,
            0x0843, 0x0443, 0x042A, 0x0452, 0x0488, 0x0434, 0x0485, 0x0478, 0x046A, 0x0435,
        )

        /**
         * Parses a `LanguageID` value (Pascal hex `$XXXX` or decimal) to its numeric LCID, or
         * `null` when it is not a valid integer literal. `$0` and `0` both parse to `0`.
         */
        fun parseId(text: String): Int? = when {
            text.startsWith("$") -> text.drop(1).toIntOrNull(16)
            else -> text.toIntOrNull()
        }

        /**
         * The curated locale entry whose [id] equals the given numeric LCID, or `null` when the LCID
         * is not part of the curated set (e.g. a valid but uncommon locale) — callers omit any
         * display in that case.
         */
        fun fromId(numericId: Int): IssWindowsLanguage? =
            entries.firstOrNull { parseId(it.id) == numericId }
    }
}

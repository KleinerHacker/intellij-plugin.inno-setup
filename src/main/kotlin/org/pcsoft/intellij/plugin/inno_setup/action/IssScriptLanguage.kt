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

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

enum class IssScriptLanguage(val displayName: String, val issName: String, val messagesFile: String) {
    ENGLISH("English", "english", "compiler:Default.isl"),
    BRAZILIAN_PORTUGUESE("Brazilian Portuguese", "brazilianportuguese", "compiler:Languages\\BrazilianPortuguese.isl"),
    CATALAN("Catalan", "catalan", "compiler:Languages\\Catalan.isl"),
    CORSICAN("Corsican", "corsican", "compiler:Languages\\Corsican.isl"),
    CZECH("Czech", "czech", "compiler:Languages\\Czech.isl"),
    DANISH("Danish", "danish", "compiler:Languages\\Danish.isl"),
    DUTCH("Dutch", "dutch", "compiler:Languages\\Dutch.isl"),
    FINNISH("Finnish", "finnish", "compiler:Languages\\Finnish.isl"),
    FRENCH("French", "french", "compiler:Languages\\French.isl"),
    GERMAN("German", "german", "compiler:Languages\\German.isl"),
    GREEK("Greek", "greek", "compiler:Languages\\Greek.isl"),
    HEBREW("Hebrew", "hebrew", "compiler:Languages\\Hebrew.isl"),
    HUNGARIAN("Hungarian", "hungarian", "compiler:Languages\\Hungarian.isl"),
    ICELANDIC("Icelandic", "icelandic", "compiler:Languages\\Icelandic.isl"),
    ITALIAN("Italian", "italian", "compiler:Languages\\Italian.isl"),
    JAPANESE("Japanese", "japanese", "compiler:Languages\\Japanese.isl"),
    KOREAN("Korean", "korean", "compiler:Languages\\Korean.isl"),
    NORWEGIAN("Norwegian", "norwegian", "compiler:Languages\\Norwegian.isl"),
    POLISH("Polish", "polish", "compiler:Languages\\Polish.isl"),
    PORTUGUESE("Portuguese", "portuguese", "compiler:Languages\\Portuguese.isl"),
    RUSSIAN("Russian", "russian", "compiler:Languages\\Russian.isl"),
    SCOTTISH_GAELIC("Scottish Gaelic", "scottishgaelic", "compiler:Languages\\ScottishGaelic.isl"),
    SERBIAN("Serbian", "serbian", "compiler:Languages\\Serbian.isl"),
    SLOVENIAN("Slovenian", "slovenian", "compiler:Languages\\Slovenian.isl"),
    SPANISH("Spanish", "spanish", "compiler:Languages\\Spanish.isl"),
    TURKISH("Turkish", "turkish", "compiler:Languages\\Turkish.isl"),
    UKRAINIAN("Ukrainian", "ukrainian", "compiler:Languages\\Ukrainian.isl"),
    ;

    /** Flag icon shown next to this language in the [Languages] completion popup. */
    val icon: Icon
        get() = IconLoader.getIcon("/icons/flags/$issName.svg", IssScriptLanguage::class.java)

    fun toIssEntry() = "Name: \"$issName\"; MessagesFile: \"$messagesFile\""

    companion object {
        /**
         * Resolves the built-in language whose [messagesFile] matches [file].
         * Comparison is case-insensitive and tolerant of `/` vs `\` separators.
         * Returns `null` when [file] is not a built-in `compiler:` messages file.
         */
        fun fromMessagesFile(file: String): IssScriptLanguage? {
            val normalized = file.normalizeMessagesFile()
            return entries.firstOrNull { it.messagesFile.normalizeMessagesFile() == normalized }
        }

        private fun String.normalizeMessagesFile(): String =
            trim().replace('\\', '/').lowercase()

        /** The built-in language whose [issName] matches [name] (case-insensitive), or `null`. */
        fun fromIssName(name: String): IssScriptLanguage? =
            entries.firstOrNull { it.issName.equals(name.trim(), ignoreCase = true) }
    }
}

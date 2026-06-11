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

package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.openapi.components.Service
import org.pcsoft.intellij.plugin.inno_setup.types.IslCodeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IslLanguageSpec

/**
 * Provides the curated Windows language identifiers loaded from `isl-code.yaml` (the single source
 * of truth for the `[LangOptions]` `LanguageID` directive): completion entries, flag/name lookup by
 * LCID, and the recognised-LCID set used for validation.
 */
@Service(Service.Level.APP)
class IssLanguageService {

    val spec: IslCodeSpec by lazy {
        val mapper = YAMLMapper.builder().addModule(kotlinModule()).build()
        val stream = IssLanguageService::class.java.getResourceAsStream("/spec/isl-code.yaml")
            ?: error("isl-code.yaml not found in resources")
        mapper.readValue(stream)
    }

    /** All recognised locales offered as completion for `LanguageID`. */
    val entries: List<IslLanguageSpec> get() = spec.languages

    /** The locales bundled with Inno Setup — the ready-to-use `[Languages]` entries. */
    val builtinLanguages: List<IslLanguageSpec> by lazy { spec.languages.filter { it.builtin } }

    /** All recognised numeric LCIDs used to validate a `LanguageID`. */
    val validIds: Set<Int> by lazy { spec.languages.mapNotNull { parseId(it.id) }.toSet() }

    /** The locale whose [IslLanguageSpec.id] equals the given numeric LCID, or `null`. */
    fun fromId(numericId: Int): IslLanguageSpec? =
        entries.firstOrNull { parseId(it.id) == numericId }

    /** The built-in language whose [IslLanguageSpec.issName] matches [name] (case-insensitive), or `null`. */
    fun fromIssName(name: String): IslLanguageSpec? =
        builtinLanguages.firstOrNull { it.issName.equals(name.trim(), ignoreCase = true) }

    /**
     * The built-in language whose [IslLanguageSpec.messagesFile] matches [file], tolerant of `/`
     * vs `\` separators and case, or `null` when [file] is not a bundled `compiler:` messages file.
     */
    fun fromMessagesFile(file: String): IslLanguageSpec? {
        val normalized = file.normalizeMessagesFile()
        return builtinLanguages.firstOrNull { it.messagesFile?.normalizeMessagesFile() == normalized }
    }

    private fun String.normalizeMessagesFile(): String = trim().replace('\\', '/').lowercase()

    companion  object {
        /**
         * Parses a `LanguageID` value (Pascal hex `$XXXX` or decimal) to its numeric LCID, or `null`
         * when it is not a valid integer literal. `$0` and `0` both parse to `0`.
         */
        fun parseId(text: String): Int? = when {
            text.startsWith("$") -> text.drop(1).toIntOrNull(16)
            else -> text.toIntOrNull()
        }
    }
}

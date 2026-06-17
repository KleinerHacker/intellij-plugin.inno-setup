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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include

import com.intellij.openapi.components.service
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.allParamPairs
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.identifiers
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.sections
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.specSection
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionAttributeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionFlagTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionType

/**
 * Collects the spec-based ("semantic") problems of an included `*.iss` fragment as plain messages, so they
 * can be surfaced on the `#include` that pulls the fragment in. Because `#include` inserts whole lines, the
 * fragment is a self-contained set of sections/entries that can be checked directly.
 *
 * Covered: unknown sections, unknown directive/parameter keys, unknown flags and duplicate flags — the
 * categories a fragment commonly introduces. Data comes from [IsSpecService] (the single source of truth),
 * so only the lightweight check logic lives here, not the validity data itself.
 */
object IsIncludeDiagnostics {

    /**
     * Returns the semantic problems of [file] as human-readable messages (without file-name prefix).
     */
    fun collect(file: IsScriptFile): List<String> {
        val spec = service<IsSpecService>().spec
        val out = mutableListOf<String>()

        file.sections.forEach { section ->
            val name = section.nameText
            if (name.equals("Code", ignoreCase = true)) return@forEach

            val specSection = section.specSection(spec)
            if (specSection == null) {
                out += "unknown section: [$name]"
                return@forEach
            }

            when (specSection.type) {
                IsSectionType.DIRECTIVE -> section.directiveEntryList.forEach { entry ->
                    val key = entry.keyText()
                    val attr = specSection.attributes.firstOrNull { it.name.equals(key, ignoreCase = true) }
                    if (attr == null) out += "unknown directive '$key' in [$name]"
                    else flagProblems(entry.paramValue, attr, out)
                }

                IsSectionType.PARAMETER -> section.allParamPairs().forEach { pair ->
                    val key = pair.keyText()
                    val attr = specSection.attributes.firstOrNull { it.name.equals(key, ignoreCase = true) }
                    if (attr == null) out += "unknown parameter '$key' in [$name]"
                    else flagProblems(pair.paramValue, attr, out)
                }

                IsSectionType.CODE -> Unit
            }
        }
        return out
    }

    /** Adds unknown-flag and duplicate-flag problems for a flags-typed [attr] value to [out]. */
    private fun flagProblems(value: IsSectionParamValue?, attr: IsSectionAttributeSpec, out: MutableList<String>) {
        val flagType = attr.type as? IsSectionFlagTypeSpec ?: return
        if (value == null) return
        val known = flagType.flags.map { it.name.lowercase() }.toSet()
        val seen = mutableSetOf<String>()
        value.identifiers.forEach { token ->
            val text = token.text
            val low = text.lowercase()
            when {
                low !in known -> out += "unknown flag: '$text'"
                !seen.add(low) -> out += "duplicate flag: '$text'"
            }
        }
    }
}

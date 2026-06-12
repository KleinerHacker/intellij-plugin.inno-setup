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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.quickfix

import org.pcsoft.intellij.plugin.inno_setup.types.*

internal object IsSectionDefaultValueGenerator {

    fun defaultFor(attr: IsSectionAttributeSpec): String =
        nameOverride(attr.name) ?: typeDefault(attr.type)

    private fun nameOverride(name: String): String? = when (name.lowercase()) {
        "appname" -> "MyApp"
        "appversion" -> "1.0"
        "root" -> "HKLM"
        "subkey" -> "Software\\MyCompany\\MyApp"
        "source" -> "MyProg.exe"
        "destdir" -> "{app}"
        "name" -> "myapp"
        "filename" -> "{app}\\MyProg.exe"
        "messagesfile" -> "compiler:Default.isl"
        "description" -> "My Description"
        "type" -> "files"
        else -> null
    }

    private fun typeDefault(type: IsSectionAttributeTypeSpec): String = when (type) {
        is IsSectionNativeTypeSpec -> when (type.dataType.lowercase()) {
            "integer" -> "0"
            "boolean" -> "yes"
            else -> "MyValue"
        }

        is IsSectionReferenceTypeSpec -> "ref"
        is IsSectionFlagTypeSpec -> type.flags.firstOrNull { it.deprecated.isEmpty() }?.name ?: "flag"
    }
}

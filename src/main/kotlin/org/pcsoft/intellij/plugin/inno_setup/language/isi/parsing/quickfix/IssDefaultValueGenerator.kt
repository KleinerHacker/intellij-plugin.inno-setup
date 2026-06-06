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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix

import org.pcsoft.intellij.plugin.inno_setup.types.IssAttributeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssAttributeTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssFlagTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssNativeTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssReferenceTypeSpec

internal object IssDefaultValueGenerator {

    fun defaultFor(attr: IssAttributeSpec): String =
        nameOverride(attr.name) ?: typeDefault(attr.type)

    private fun nameOverride(name: String): String? = when (name.lowercase()) {
        "appname"      -> "MyApp"
        "appversion"   -> "1.0"
        "root"         -> "HKLM"
        "subkey"       -> "Software\\MyCompany\\MyApp"
        "source"       -> "MyProg.exe"
        "destdir"      -> "{app}"
        "name"         -> "myapp"
        "filename"     -> "{app}\\MyProg.exe"
        "messagesfile" -> "compiler:Default.isl"
        "description"  -> "My Description"
        "type"         -> "files"
        else           -> null
    }

    private fun typeDefault(type: IssAttributeTypeSpec): String = when (type) {
        is IssNativeTypeSpec    -> when (type.dataType.lowercase()) {
            "integer" -> "0"
            "boolean" -> "yes"
            else      -> "MyValue"
        }
        is IssReferenceTypeSpec -> "ref"
        is IssFlagTypeSpec      -> type.flags.firstOrNull { !it.deprecated }?.name ?: "flag"
    }
}

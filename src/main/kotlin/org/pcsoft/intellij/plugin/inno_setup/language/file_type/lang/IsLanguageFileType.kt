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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptLanguage
import javax.swing.Icon

/**
 * File type for Inno Setup language files (`.isl`). Bound to the existing [IsScriptLanguage] so all ISS
 * language extensions (highlighting, completion, navigation, …) apply automatically; only the
 * allowed sections differ (enforced in `language/isl/parsing`).
 */
class IsLanguageFileType private constructor() : LanguageFileType(IsScriptLanguage) {
    companion object {
        @JvmField
        val INSTANCE = IsLanguageFileType()
    }

    override fun getName(): String = "Inno Setup Language"
    override fun getDescription(): String = "Inno Setup language (translation) file"
    override fun getDefaultExtension(): String = "isl"
    override fun getIcon(): Icon = IsIcons.ScriptFile
}

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

package org.pcsoft.intellij.plugin.inno_setup.script.language.file_type

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * Defines the IntelliJ file type metadata for this Inno Setup language file.
 */
class IsScriptFileType : LanguageFileType(IsScriptLanguage) {
    companion object {
        @JvmField
        val INSTANCE = IsScriptFileType()
    }

    /**
     * Returns the logical name exposed by this PSI element.
     */
    override fun getName() = "Inno Setup Script"

    /**
     * User-visible presentation name (Settings ▸ Editor ▸ File Types). Must be unique across file types
     * that share the same language; the platform (since 2026.1) rejects duplicate display names.
     */
    override fun getDisplayName() = "Inno Setup Script"

    /**
     * Returns user-visible presentation text for this IntelliJ extension.
     */
    override fun getDescription() = "Inno Setup script file"

    /**
     * Returns the default file extension for this file type.
     */
    override fun getDefaultExtension() = "iss"

    /**
     * Returns the icon shown for this element or file type.
     */
    override fun getIcon(): Icon = IsIcons.ScriptFile
}

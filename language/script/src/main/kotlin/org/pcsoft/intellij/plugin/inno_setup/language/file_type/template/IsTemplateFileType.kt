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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.template

import com.intellij.openapi.fileTypes.LanguageFileType
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import javax.swing.Icon

/**
 * File type of `.ist` (Inno Setup Template) files — free text usable as an `#include` target.
 */
class IsTemplateFileType private constructor() : LanguageFileType(IsTemplateLanguage) {
    companion object {
        @JvmField
        val INSTANCE = IsTemplateFileType()
    }

    override fun getName(): String = "Inno Setup Template"
    override fun getDescription(): String = "Inno Setup template (free-text include) file"
    override fun getDefaultExtension(): String = "ist"
    override fun getIcon(): Icon = IsIcons.TemplateFile
}

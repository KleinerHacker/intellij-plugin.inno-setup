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

package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.openapi.fileTypes.LanguageFileType
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import javax.swing.Icon

class IssFileType : LanguageFileType(IssLanguage) {
    companion object {
        @JvmField
        val INSTANCE = IssFileType()
    }

    override fun getName() = "Inno Setup Script"
    override fun getDescription() = "Inno Setup script file"
    override fun getDefaultExtension() = "iss"
    override fun getIcon(): Icon = IssIcons.ScriptFile
}

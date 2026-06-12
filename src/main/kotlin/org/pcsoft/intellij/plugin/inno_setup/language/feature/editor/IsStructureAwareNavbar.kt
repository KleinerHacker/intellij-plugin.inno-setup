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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.displayName
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParameterEntry
import javax.swing.Icon

class IsStructureAwareNavbar : StructureAwareNavBarModelExtension() {
    override val language: Language
        get() = IsScriptLanguage

    override fun getPresentableText(obj: Any?): String? {
        return when (obj) {
            is IsScriptFile -> obj.name
            is IsSectionBlock -> obj.nameText
            is IsSectionDirectiveEntry -> obj.keyText()
            is IsSectionParameterEntry -> obj.displayName
            else -> null
        }
    }

    override fun getIcon(obj: Any?): Icon? = when (obj) {
        is IsSectionBlock -> IsIcons.Section
        is IsSectionDirectiveEntry -> IsIcons.ParameterEntry
        is IsSectionParameterEntry -> IsIcons.ParameterEntry
        else -> null
    }
}
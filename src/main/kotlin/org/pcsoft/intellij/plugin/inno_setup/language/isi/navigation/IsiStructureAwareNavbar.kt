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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.isi.displayName
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiSection
import javax.swing.Icon

class IsiStructureAwareNavbar : StructureAwareNavBarModelExtension() {
    override val language: Language
        get() = IssLanguage

    override fun getPresentableText(obj: Any?): String? {
        return when (obj) {
            is IssFile -> obj.name
            is IsiSection -> obj.nameText
            is IsiDirectiveEntry -> obj.keyText()
            is IsiParameterEntry -> obj.displayName
            else -> null
        }
    }

    override fun getIcon(obj: Any?): Icon? = when (obj) {
        is IsiSection -> IssIcons.Section
        is IsiDirectiveEntry -> IssIcons.ParameterEntry
        is IsiParameterEntry -> IssIcons.ParameterEntry
        else -> null
    }
}
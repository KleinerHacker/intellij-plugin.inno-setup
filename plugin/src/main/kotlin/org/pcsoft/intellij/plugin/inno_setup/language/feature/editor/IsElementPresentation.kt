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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.displayName
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParameterEntry
import javax.swing.Icon

/**
 * Shared label and icon of an Inno Setup structure element, used by every place that presents the script
 * structure in the UI: navigation bar and breadcrumbs.
 */
object IsElementPresentation {

    /** The label of [obj], or `null` if it is not a presentable structure element. */
    fun textOf(obj: Any?): String? = when (obj) {
        is IsScriptFile -> obj.name
        is IsSectionBlock -> obj.nameText
        is IsSectionDirectiveEntry -> obj.keyText()
        is IsSectionParameterEntry -> obj.displayName
        else -> null
    }

    /** The icon of [obj], or `null` if it has none. */
    fun iconOf(obj: Any?): Icon? = when (obj) {
        is IsSectionBlock -> IsIcons.Section
        is IsSectionDirectiveEntry -> IsIcons.ParameterEntry
        is IsSectionParameterEntry -> IsIcons.ParameterEntry
        else -> null
    }
}

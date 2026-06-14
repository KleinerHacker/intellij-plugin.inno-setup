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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.script

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.LayeredIcon
import com.intellij.util.IconUtil
import javax.swing.Icon

/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
object IsIcons {
    /**
     * Represents the PSI script file for this Inno Setup language.
     */
    @JvmField
    val ScriptFile: Icon = IconLoader.getIcon("/icons/inno-setup-script-icon@16.png", IsIcons::class.java)

    /**
     * Represents the PSI language file for this Inno Setup language.
     */
    @JvmField
    val LanguageFile: Icon = IconLoader.getIcon("/icons/inno-setup-lang-icon@16.png", IsIcons::class.java)

    /**
     * Icon for the Inno Setup run configuration: the script icon (from `inno-setup-script-icon.png`,
     * scaled to 16px) with the standard green "run" play arrow overlaid as a badge in the lower-right
     * corner.
     */
    @JvmField
    val RunConfiguration: Icon = run {
        val raw = IconLoader.getIcon("/icons/inno-setup-script-icon.png", IsIcons::class.java)
        val base = if (raw.iconWidth > 0) IconUtil.scale(raw, null, 16f / raw.iconWidth) else raw
        val badge = IconUtil.scale(AllIcons.RunConfigurations.TestState.Run, null, 0.7f)
        LayeredIcon(2).apply {
            setIcon(base, 0)
            setIcon(badge, 1, base.iconWidth - badge.iconWidth, base.iconHeight - badge.iconHeight)
        }
    }

    /**
     * Provides Inno Setup plugin behavior for the IntelliJ Platform.
     */
    @JvmField
    val Section: Icon = AllIcons.Nodes.Class

    /**
     * Returns or performs the public behavior represented by this member.
     */
    @JvmField
    val ParameterEntry: Icon = AllIcons.Nodes.Field

    /**
     * Returns or performs the public behavior represented by this member.
     */
    @JvmField
    val Constant: Icon = AllIcons.Nodes.Static

    /**
     * Returns or performs the public behavior represented by this member.
     */
    @JvmField
    val Variable: Icon = AllIcons.Nodes.Variable
}

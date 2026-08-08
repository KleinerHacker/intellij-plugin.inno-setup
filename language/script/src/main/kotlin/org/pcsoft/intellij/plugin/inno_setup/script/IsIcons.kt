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

package org.pcsoft.intellij.plugin.inno_setup.script

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.LayeredIcon
import org.pcsoft.intellij.plugin.inno_setup.script.types.IsSectionType
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.Icon
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
object IsIcons {
    /**
     * Represents the PSI script file for this Inno Setup language.
     */
    @JvmField
    val ScriptFile: Icon = IconLoader.getIcon("/icons/inno-setup-script-icon.svg", IsIcons::class.java)

    /**
     * Represents the PSI language file for this Inno Setup language.
     */
    @JvmField
    val LanguageFile: Icon = IconLoader.getIcon("/icons/inno-setup-lang-icon.svg", IsIcons::class.java)

    /**
     * Represents the PSI template file for this Inno Setup language.
     */
    @JvmField
    val TemplateFile: Icon = IconLoader.getIcon("/icons/inno-setup-template-icon.svg", IsIcons::class.java)

    /**
     * Icon for the Inno Setup run configuration: the script icon with the standard green "run" play arrow overlaid as a badge that covers 50% of
     * the base icon's area in the lower-right corner.
     */
    @JvmField
    val RunConfiguration: Icon = run {
        val base = IconLoader.getIcon("/icons/inno-setup-script-icon.svg", IsIcons::class.java)
        // 50% of the base area -> each side is scaled by sqrt(0.5) of the base side.
        val targetSide = (base.iconWidth * sqrt(0.5)).roundToInt()
        val badge = ScaledIcon(AllIcons.RunConfigurations.TestState.Run, targetSide)
        LayeredIcon(2).apply {
            setIcon(base, 0)
            setIcon(
                badge, 1, (base.iconWidth - badge.iconWidth / 1.5).toInt(),
                (base.iconHeight - badge.iconHeight / 1.5).toInt()
            )
        }
    }

    /**
     * Wraps [delegate] and renders it at a fixed square [size] by applying a `Graphics2D` transform
     * during paint. Unlike `IconUtil.scale`, this reliably scales the painted pixels (not just the
     * reported icon bounds), so corner badges actually shrink.
     */
    private class ScaledIcon(private val delegate: Icon, private val size: Int) : Icon {
        override fun getIconWidth(): Int = size
        override fun getIconHeight(): Int = size

        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            val g2 = g.create() as Graphics2D
            try {
                g2.translate(x, y)
                g2.scale(size.toDouble() / delegate.iconWidth, size.toDouble() / delegate.iconHeight)
                delegate.paintIcon(c, g2, 0, 0)
            } finally {
                g2.dispose()
            }
        }
    }

    /**
     * Represents a `[…]` section block in the structure view, breadcrumbs and navigation bar.
     */
    @JvmField
    val Section: Icon = IconLoader.getIcon("/icons/inno-setup-section-icon.svg", IsIcons::class.java)

    /**
     * Represents a `Key: Value` parameter entry of a parameter section such as \[Files], carrying the `:`
     * separator as its glyph.
     */
    @JvmField
    val ParameterEntry: Icon = IconLoader.getIcon("/icons/inno-setup-parameter-icon.svg", IsIcons::class.java)

    /**
     * Represents a `Key=Value` directive entry of a directive section such as \[Setup], carrying the `=`
     * separator as its glyph.
     */
    @JvmField
    val DirectiveEntry: Icon = IconLoader.getIcon("/icons/inno-setup-directive-icon.svg", IsIcons::class.java)

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

    /**
     * Icon for built-in ISPP functions offered in completion.
     */
    @JvmField
    val Function: Icon = AllIcons.Nodes.Method

    /**
     * The icon characterising a section by the entry syntax it uses: `=` for a directive section such as
     * \[Setup], `:` for a parameter section such as \[Files], and the script icon for the free-form Pascal
     * \[Code] section.
     *
     * Used wherever a section is presented before its entries are visible — the section-name completion and
     * the inlay hint inside the `[…]` header — so the syntax of the section is known before the first entry
     * is typed. An unknown [type] (a section the specification does not describe) gets no icon.
     */
    fun forSectionType(type: IsSectionType?): Icon? = when (type) {
        IsSectionType.DIRECTIVE -> DirectiveEntry
        IsSectionType.PARAMETER -> ParameterEntry
        IsSectionType.CODE -> ScriptFile
        null -> null
    }
}

package org.pcsoft.intellij.plugin.inno_setup

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object IssIcons {
    @JvmField val ScriptFile: Icon = IconLoader.getIcon("/icons/inno-setup-script-icon@16.png", IssIcons::class.java)
    @JvmField val Section: Icon = AllIcons.Nodes.Class
    @JvmField val ParameterEntry: Icon = AllIcons.Nodes.Field
    @JvmField val Constant: Icon = AllIcons.Nodes.Static
    @JvmField val Variable: Icon = AllIcons.Nodes.Variable
}

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

package org.pcsoft.intellij.plugin.inno_setup

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object IssIcons {
    @JvmField
    val ScriptFile: Icon = IconLoader.getIcon("/icons/inno-setup-script-icon@16.png", IssIcons::class.java)
    @JvmField
    val Section: Icon = AllIcons.Nodes.Class
    @JvmField
    val ParameterEntry: Icon = AllIcons.Nodes.Field
    @JvmField
    val Constant: Icon = AllIcons.Nodes.Static
    @JvmField
    val Variable: Icon = AllIcons.Nodes.Variable
}

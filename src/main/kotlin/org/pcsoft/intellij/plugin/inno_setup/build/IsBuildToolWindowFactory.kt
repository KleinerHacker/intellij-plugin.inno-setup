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

package org.pcsoft.intellij.plugin.inno_setup.build

import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

/**
 * Registers the "Inno Setup" tool window that hosts the compiler [com.intellij.execution.ui.ConsoleView]
 * owned by [IsBuildConsoleService]. The factory attaches that console as the tool window's content the
 * first time the window is shown. Registered in plugin.xml.
 */
class IsBuildToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val view = project.service<IsBuildConsoleService>().obtainConsole() ?: return
        if (toolWindow.contentManager.contentCount > 0) return
        val content = ContentFactory.getInstance().createContent(view.component, "", false)
        content.isCloseable = false
        toolWindow.contentManager.addContent(content)
    }
}

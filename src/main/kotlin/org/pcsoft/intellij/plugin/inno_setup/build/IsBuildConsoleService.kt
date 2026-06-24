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

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindowManager

/** The id of the build console tool window; must match the `<toolWindow>` registration in plugin.xml. */
const val IS_BUILD_TOOL_WINDOW_ID = "Inno Setup"

/**
 * Owns the single [ConsoleView] that shows Inno Setup compiler output, hosted in the "Inno Setup" tool
 * window. Uses only public platform APIs (console builder, tool window manager, content factory) — no
 * internal Build-tool-window/event APIs. The console is created lazily on the EDT and disposed with the
 * project (this service). In headless/unit-test mode no UI is created and all output is dropped.
 */
@Service(Service.Level.PROJECT)
class IsBuildConsoleService(private val project: Project) : Disposable {

    @Volatile
    private var console: ConsoleView? = null

    private fun headless(): Boolean =
        ApplicationManager.getApplication().let { it.isUnitTestMode || it.isHeadlessEnvironment }

    /** Lazily creates the console on the EDT, wiring in the navigation filter; returns `null` when headless. */
    fun obtainConsole(): ConsoleView? {
        if (headless()) return null
        console?.let { return it }
        var result: ConsoleView? = null
        ApplicationManager.getApplication().invokeAndWait {
            console?.let { result = it; return@invokeAndWait }
            if (project.isDisposed) return@invokeAndWait
            val view = TextConsoleBuilderFactory.getInstance().createBuilder(project)
                .apply { addFilter(IsBuildOutputFilter(project)) }
                .console
            Disposer.register(this, view)
            console = view
            result = view
        }
        return result
    }

    /**
     * Clears the console and brings the "Inno Setup" tool window to the front for a fresh build run.
     * Returns the console (or `null` when headless), so the caller can stream output into it.
     */
    fun start(): ConsoleView? {
        val view = obtainConsole() ?: return null
        view.clear()
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed) return@invokeLater
            // Activating the tool window triggers IsBuildToolWindowFactory, which attaches this console.
            ToolWindowManager.getInstance(project).getToolWindow(IS_BUILD_TOOL_WINDOW_ID)?.activate(null)
        }
        return view
    }

    /** Prints a line to the build console, routed to stderr (errors) or stdout (everything else). */
    fun print(view: ConsoleView?, text: String, error: Boolean) {
        view?.print(
            text,
            if (error) ConsoleViewContentType.ERROR_OUTPUT else ConsoleViewContentType.NORMAL_OUTPUT,
        )
    }

    override fun dispose() {
        console = null
    }
}

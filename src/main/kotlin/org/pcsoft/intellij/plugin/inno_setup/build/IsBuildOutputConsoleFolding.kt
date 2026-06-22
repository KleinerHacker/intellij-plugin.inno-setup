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

import com.intellij.execution.ConsoleFolding
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project

/**
 * Folds the indented detail lines of the ISCC compiler output in the Build tool window console
 * (e.g. the `   Reading file: …` / `   Messages in script file` lines below a `\[Languages]`
 * section). Consecutive indented lines are merged by the platform into a single, collapsed-by-default
 * region whose placeholder shows the number of hidden lines.
 *
 * Section-level grouping (one tree node per `\[Section]`) is produced separately by
 * [IsBuildOutputGrouper]; this folding only handles the in-console detail lines.
 */
class IsBuildOutputConsoleFolding : ConsoleFolding() {

    /** Detail lines are exactly those ISCC indents with leading whitespace. */
    override fun shouldFoldLine(project: Project, line: String): Boolean =
        line.isNotEmpty() && line[0].isWhitespace() && line.isNotBlank()

    override fun getPlaceholderText(project: Project, lines: List<String>): String =
        " … (${lines.size} ${if (lines.size == 1) "Zeile" else "Zeilen"})"

    /** Restrict to the Inno Setup build console; other consoles keep their own folding. */
    override fun isEnabledForConsole(consoleView: ConsoleView): Boolean =
        consoleView.javaClass.name.contains("build", ignoreCase = true)
}

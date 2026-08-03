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

package org.pcsoft.intellij.plugin.inno_setup.build.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.script.build.IsScriptCollector

/**
 * Shared selection and enablement rules of the `.iss` context-menu entries, so the run and build variants —
 * and the submenus that group them by build configuration — cannot drift apart in when they are offered.
 */
object IsScriptActionSupport {

    /** The single selected `.iss` file, or `null` for any other selection. */
    fun singleScript(e: AnActionEvent): VirtualFile? {
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        if (files != null && files.size > 1) return null
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return null
        if (file.isDirectory || !file.extension.equals("iss", ignoreCase = true)) return null
        return file
    }

    /**
     * Applies the common visibility/enablement rules to [presentation]: hidden unless exactly one `.iss`
     * file is selected in a project, and disabled — with [includedReason] as the hover text — for a script
     * that another script includes and that therefore must not be built standalone.
     *
     * @return the selected script when the action is enabled, else `null`
     */
    fun updatePresentation(
        e: AnActionEvent,
        presentation: Presentation,
        includedReason: String,
        description: String
    ): VirtualFile? {
        val project = e.project
        val file = singleScript(e)
        if (project == null || file == null) {
            presentation.isVisible = false
            return null
        }
        presentation.isVisible = true
        return if (IsScriptCollector(project).isIncludedByOther(file)) {
            presentation.isEnabled = false
            presentation.description = includedReason
            null
        } else {
            presentation.isEnabled = true
            presentation.description = description
            file
        }
    }
}

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

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import org.pcsoft.intellij.plugin.inno_setup.build.IsCompilerService
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle

/**
 * Compiles a single `.iss` script with ISCC using one named build configuration. Not registered in
 * `plugin.xml` but instantiated per build configuration by [IsScriptBuildGroup]. It is greyed out — with
 * the reason as its hover description — for scripts that are included by another script and therefore must
 * not be built standalone.
 *
 * @param buildConfigName name of the build configuration to compile with; resolved at invocation time so
 *                        the menu entry always uses the configuration's current content
 */
class IsScriptBuildAction(private val buildConfigName: String) :
    DumbAwareAction(
        { buildConfigName },
        { PluginBundle.message("action.iss.build_script.description") },
        IsBuildConfigurationActions.ICON
    ) {

    /**
     * Runs on a background thread because the enabled-state check inspects the project's `#include`
     * graph via the index.
     */
    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    /**
     * Shows the action only for a single `.iss` file; disables it (with a reason) for included scripts.
     */
    override fun update(e: AnActionEvent) {
        IsScriptActionSupport.updatePresentation(
            e,
            e.presentation,
            PluginBundle.message("action.iss.build_script.included_reason"),
            PluginBundle.message("action.iss.build_script.description")
        )
    }

    /**
     * Compiles the selected script with the configuration this entry stands for.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = IsScriptActionSupport.singleScript(e) ?: return
        val config = IsBuildConfigurationService.getInstance(project).byName(buildConfigName)
        project.service<IsCompilerService>().compileScript(file, config)
    }
}

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

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import javax.swing.Icon

/**
 * Shared pieces of the build-configuration driven context-menu entries.
 */
object IsBuildConfigurationActions {

    /** The gear that marks anything build-configuration related, from the submenus to the run popup. */
    val ICON: Icon = AllIcons.General.GearPlain

    /**
     * The build configurations to offer in a menu, creating the project's starter set on first use so the
     * submenu is never empty for a project that simply has not been initialised yet.
     */
    fun availableConfigurations(project: Project): List<IsBuildConfiguration> {
        val service = IsBuildConfigurationService.getInstance(project)
        service.ensureDefaults()
        return service.all()
    }
}

/**
 * Base class of the `.iss` context-menu submenus: visible for a single, non-included script and populated
 * with one child action per build configuration.
 *
 * A submenu rather than a flat entry because the build configuration is the one thing an invocation from the
 * context menu still has to be told — everything else is already determined by the selected script.
 *
 * @param textKey           bundle key of the submenu label
 * @param includedReasonKey bundle key of the hover text explaining why an included script is disabled
 * @param descriptionKey    bundle key of the hover text of the enabled submenu
 */
abstract class IsBuildConfigurationActionGroup(
    textKey: String,
    private val includedReasonKey: String,
    private val descriptionKey: String
) : ActionGroup(), DumbAware {

    init {
        isPopup = true
        templatePresentation.text = PluginBundle.message(textKey)
        templatePresentation.icon = IsBuildConfigurationActions.ICON
    }

    /** The action executed when the user picks [config] from the submenu. */
    protected abstract fun createAction(config: IsBuildConfiguration): AnAction

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        IsScriptActionSupport.updatePresentation(
            e,
            e.presentation,
            PluginBundle.message(includedReasonKey),
            PluginBundle.message(descriptionKey)
        )
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val project = e?.project ?: return EMPTY_ARRAY
        return IsBuildConfigurationActions.availableConfigurations(project)
            .map { createAction(it) }
            .toTypedArray()
    }
}

/**
 * Submenu listing every build configuration; picking one compiles and runs the selected script with it.
 */
class IsScriptRunGroup : IsBuildConfigurationActionGroup(
    textKey = "action.iss.run_script.text",
    includedReasonKey = "action.iss.run_script.included_reason",
    descriptionKey = "action.iss.run_script.description"
) {
    override fun createAction(config: IsBuildConfiguration): AnAction = IsScriptRunAction(config.name)
}

/**
 * Submenu listing every build configuration; picking one compiles the selected script with it.
 */
class IsScriptBuildGroup : IsBuildConfigurationActionGroup(
    textKey = "action.iss.build_script.text",
    includedReasonKey = "action.iss.build_script.included_reason",
    descriptionKey = "action.iss.build_script.description"
) {
    override fun createAction(config: IsBuildConfiguration): AnAction = IsScriptBuildAction(config.name)
}

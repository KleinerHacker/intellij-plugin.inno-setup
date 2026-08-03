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

package org.pcsoft.intellij.plugin.inno_setup.build.config

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.execution.RunManagerListener
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorNotifications
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSymbolTracker

/**
 * Re-runs the conditional-branch analysis whenever the selected run configuration changes.
 *
 * Since the selected configuration decides which build configuration seeds the `#ifdef` symbols
 * ([org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfigurationSymbolProvider]), switching from
 * `Debug` to `Release` in the toolbar must repaint the greyed-out branches — otherwise the editor would keep
 * showing the previous configuration's outcome until the file is touched.
 *
 * A daemon restart alone is not enough: the analysis is cached per document revision, so the restarted pass
 * would re-read the previous result. [IsPreprocessorSymbolTracker] is therefore bumped first.
 */
class IsRunConfigurationSelectionListener(private val project: Project) : RunManagerListener {

    override fun runConfigurationSelected(settings: RunnerAndConfigurationSettings?) = refresh()

    override fun runConfigurationChanged(settings: RunnerAndConfigurationSettings) = refresh()

    override fun runConfigurationRemoved(settings: RunnerAndConfigurationSettings) = refresh()

    private fun refresh() {
        if (project.isDisposed) return
        IsPreprocessorSymbolTracker.getInstance(project).symbolsChanged()
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed) return@invokeLater
            DaemonCodeAnalyzer.getInstance(project).restart(RESTART_REASON)
            EditorNotifications.getInstance(project).updateAllNotifications()
        }
    }

    private companion object {
        /** Diagnostic reason shown in the daemon's restart log. */
        const val RESTART_REASON = "Inno Setup run configuration selection changed"
    }
}

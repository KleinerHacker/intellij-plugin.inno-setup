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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.Computable
import org.pcsoft.intellij.plugin.inno_setup.script.build.IsScriptCollector

/**
 * Creates the starter build configurations (`Release` and `Debug`) the first time a project containing
 * Inno Setup scripts is opened.
 *
 * Gated on the project actually holding a `.iss` file so an unrelated project never gets a stray `.build`
 * directory just because the plugin happens to be installed.
 */
class IsBuildConfigurationStartupActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        if (ApplicationManager.getApplication().isUnitTestMode) return
        val service = IsBuildConfigurationService.getInstance(project)
        if (service.all().isNotEmpty()) return

        DumbService.getInstance(project).waitForSmartMode()
        val hasScripts = ApplicationManager.getApplication().runReadAction(Computable {
            IsScriptCollector(project).allScripts().isNotEmpty()
        })
        if (hasScripts) service.ensureDefaults()
    }
}

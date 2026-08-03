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

package org.pcsoft.intellij.plugin.inno_setup.build.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.Project
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService

/**
 * Creates [IsRunConfiguration]s. A new configuration starts on the `Debug` build configuration, so running
 * a script straight away compiles with `DEBUG` defined — the expectation set by the C/C++ toolchains this
 * follows. The starter configurations are created on the spot if the project has none yet.
 */
class IsRunConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {

    override fun getId() = IsRunConfigurationType.ID

    override fun createTemplateConfiguration(project: Project): IsRunConfiguration {
        val config = IsRunConfiguration(project, this, "")
        val service = IsBuildConfigurationService.getInstance(project)
        service.ensureDefaults()
        config.buildConfigurationName = service.byName(IsBuildConfiguration.DEFAULT_DEBUG_NAME)?.name
            ?: service.defaultConfiguration()?.name
                    ?: ""
        return config
    }
}

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

import com.intellij.execution.configurations.ConfigurationTypeBase
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsIcons

class IsRunConfigurationType : ConfigurationTypeBase(
    "IssRunConfiguration",
    PluginBundle.message("run.config.type.name"),
    PluginBundle.message("run.config.type.name"),
    IsIcons.RunConfiguration
) {
    init {
        addFactory(IsRunConfigurationFactory(this))
    }

    companion object {
        const val ID = "IssRunConfiguration"
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

/**
 * Project-level persistent settings service for the Inno Setup build integration.
 */
@State(name = "IssBuildSettings", storages = [Storage("inno-setup.xml")])
@Service(Service.Level.PROJECT)
class IsBuildSettingsService : SimplePersistentStateComponent<IsBuildSettingsState>(IsBuildSettingsState()) {

    companion object {
        /**
         * Returns the build-settings service for [project].
         */
        @JvmStatic
        fun getInstance(project: Project): IsBuildSettingsService = project.service()
    }
}

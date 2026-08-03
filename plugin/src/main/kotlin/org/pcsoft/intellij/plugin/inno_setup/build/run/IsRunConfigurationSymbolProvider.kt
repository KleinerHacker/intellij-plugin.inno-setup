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

import com.intellij.execution.RunManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSymbolProvider

/**
 * Contributes the ISCC `/D…` symbols of the **selected** run configuration, so the editor decides `#ifdef`
 * branches the same way the next run will.
 *
 * The selected configuration is the honest source: it names exactly one build configuration, and that is
 * what the user is about to compile with. Consulting every configuration instead — as this did while the
 * symbols still lived on the run configuration itself — could only ever contribute what a "Debug"/"Release"
 * pair agreed on, which is the empty set precisely for the symbols that matter.
 *
 * When no Inno Setup configuration is selected nothing is contributed, and
 * [org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationNotificationProvider] tells the
 * user why the conditions in front of them stay undecided.
 */
class IsRunConfigurationSymbolProvider : IsPreprocessorSymbolProvider {

    override fun symbols(project: Project, script: VirtualFile?): Map<String, String?> =
        selectedConfiguration(project)?.buildConfiguration()?.symbols ?: emptyMap()

    companion object {

        /** The selected run configuration when it is an Inno Setup one, else `null`. */
        fun selectedConfiguration(project: Project): IsRunConfiguration? =
            RunManager.getInstance(project).selectedConfiguration?.configuration as? IsRunConfiguration
    }
}

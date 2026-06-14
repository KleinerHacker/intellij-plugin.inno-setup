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

import com.intellij.openapi.components.BaseState
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode

/**
 * Project-specific persistent state for the Inno Setup build integration. Unlike the IDE-wide
 * installation path/version (see [IsSettingsState]), these settings differ per project.
 */
class IsBuildSettingsState : BaseState() {
    /**
     * Whether `.iss` scripts are compiled with ISCC when this project is built.
     */
    var compileOnBuild: Boolean by property(true)

    /**
     * Where ISCC writes its output, persisted as the [IsBuildOutputMode] enum name.
     */
    var outputMode: String? by string(IsBuildOutputMode.DEFAULT.name)

    /**
     * Last successfully built MD5 hash per top-level script and output target (key = canonical script
     * path + `|` + the resolved ISCC `/O…` argument). Used to skip recompiling unchanged scripts —
     * both during the regular project build and before a run — unless a rebuild is forced. Including
     * the output target keeps build- and run-outputs tracked independently.
     */
    var buildHashes: MutableMap<String, String> by map()
}

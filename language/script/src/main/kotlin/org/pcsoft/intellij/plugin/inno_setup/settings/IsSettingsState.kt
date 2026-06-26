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

/**
 * IDE-wide persistent state for the plugin's Inno Setup configuration. Project-specific build
 * options live in [org.pcsoft.intellij.plugin.inno_setup.settings.IsBuildSettingsState].
 */
class IsSettingsState : BaseState() {
    /**
     * Installation directory that should contain `ISCC.exe` and `Compil32.exe`.
     */
    var installationPath: String? by string()

    /**
     * Minimum Inno Setup version selected for version-aware validation, or `null` when unset.
     */
    var minInnoVersion: String? by string()

    /**
     * Whether the resolved content of an `#include "…"` directive is shown as an inlay hint above the
     * directive line. Enabled by default.
     */
    var showIncludeContentInlayHints: Boolean by property(true)
}

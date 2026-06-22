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

package org.pcsoft.intellij.plugin.inno_setup.build

import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode.Companion.DEFAULT


/**
 * Where ISCC should write its output when compiling on project build. The three modes are mutually
 * exclusive and are surfaced in the settings as a single combo box so they cannot be combined into
 * an invalid state.
 */
enum class IsBuildOutputMode(
    /** Human-readable label shown in the settings combo box. */
    val label: String
) {
    /** Leave the script's `[Setup] OutputDir` untouched (no `/O` switch). */
    SCRIPT("As defined in script"),

    /** Redirect a relative `OutputDir` under the project build directory (`out`/`target`/`build`). */
    BUILD_DIR("Into project build directory"),

    /** Validate only — pass the native ISCC `/O-` switch so no setup file is produced. */
    DRY("Dry build — validate only, no output");

    companion object {
        /** The default mode used when nothing is persisted yet. */
        val DEFAULT = BUILD_DIR

        /**
         * Resolves a persisted [name] back to a mode, falling back to [DEFAULT] for unknown or
         * `null` values so a corrupted setting never breaks the build.
         */
        fun fromName(name: String?): IsBuildOutputMode =
            entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}

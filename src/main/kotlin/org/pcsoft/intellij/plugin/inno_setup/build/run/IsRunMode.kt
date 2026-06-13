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

/** Whether the installer should be launched in debug mode or for real. */
enum class IsRunMode {
    /** Run the installer with `/VERYSILENT /SUPPRESSMSGBOXES` for automated testing. */
    DRY,

    /** Run the installer normally (shows the UI). */
    REAL;

    companion object {
        val DEFAULT = DRY
        fun fromName(name: String?): IsRunMode = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}

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

import org.junit.Assert.*
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode

class IsBuildSettingsStateTest {

    @Test
    fun `defaults are compile-on and build-dir`() {
        val state = IsBuildSettingsState()
        assertTrue(state.compileOnBuild)
        assertEquals(IsBuildOutputMode.BUILD_DIR.name, state.outputMode)
        assertEquals(IsBuildOutputMode.BUILD_DIR, IsBuildOutputMode.fromName(state.outputMode))
    }

    @Test
    fun `fields round-trip`() {
        val state = IsBuildSettingsState()
        state.compileOnBuild = false
        state.outputMode = IsBuildOutputMode.DRY.name
        assertFalse(state.compileOnBuild)
        assertEquals(IsBuildOutputMode.DRY, IsBuildOutputMode.fromName(state.outputMode))
    }
}

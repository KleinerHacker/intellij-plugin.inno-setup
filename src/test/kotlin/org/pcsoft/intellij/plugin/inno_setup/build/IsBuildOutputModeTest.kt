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

import org.junit.Assert.*
import org.junit.Test

class IsBuildOutputModeTest {

    @Test
    fun `default is build dir`() {
        assertEquals(IsBuildOutputMode.BUILD_DIR, IsBuildOutputMode.DEFAULT)
    }

    @Test
    fun `all modes have a non-blank label`() {
        IsBuildOutputMode.entries.forEach { assertTrue(it.label.isNotBlank()) }
    }

    @Test
    fun `fromName round-trips every enum name`() {
        IsBuildOutputMode.entries.forEach {
            assertEquals(it, IsBuildOutputMode.fromName(it.name))
        }
    }

    @Test
    fun `fromName falls back to default for unknown or null`() {
        assertEquals(IsBuildOutputMode.DEFAULT, IsBuildOutputMode.fromName(null))
        assertEquals(IsBuildOutputMode.DEFAULT, IsBuildOutputMode.fromName("nonsense"))
    }
}

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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.script.build.IsScriptCollector

/**
 * Tests the pure `#include` extraction of [IsScriptCollector].
 */
class IsScriptCollectorParseTest {

    @Test
    fun `quoted include is extracted`() {
        assertEquals(listOf("common.iss"), IsScriptCollector.parseIncludes("""#include "common.iss" """))
    }

    @Test
    fun `angle bracket include is extracted`() {
        assertEquals(listOf("shared\\base.iss"), IsScriptCollector.parseIncludes("#include <shared\\base.iss>"))
    }

    @Test
    fun `unquoted include is extracted`() {
        assertEquals(listOf("fragment.iss"), IsScriptCollector.parseIncludes("#include fragment.iss"))
    }

    @Test
    fun `spacing variants and multiple includes`() {
        val text = """
            [Setup]
            #  include "a.iss"
            ; comment
            #include "b.iss"
        """.trimIndent()
        assertEquals(listOf("a.iss", "b.iss"), IsScriptCollector.parseIncludes(text))
    }

    @Test
    fun `no includes returns empty`() {
        assertTrue(IsScriptCollector.parseIncludes("[Setup]\nAppName=x").isEmpty())
    }
}

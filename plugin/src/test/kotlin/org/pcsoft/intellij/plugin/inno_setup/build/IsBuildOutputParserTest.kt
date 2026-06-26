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

class IsBuildOutputParserTest {

    @Test
    fun `error with line and file is parsed and converted to zero-based`() {
        val raw = "Error on line 23 in C:\\proj\\setup.iss: Unknown directive"
        val line = IsBuildOutputParser.parseLine(raw)
        assertTrue(line is IsBuildOutputParser.Line.Problem)
        val p = line as IsBuildOutputParser.Line.Problem
        assertEquals("Unknown directive", p.message)
        assertEquals("full raw line is kept as detail", raw, p.detail)
        assertEquals("C:\\proj\\setup.iss", p.file)
        assertEquals(22, p.line)            // 23 → 22 (0-based)
        assertNull(p.column)
        assertFalse(p.warning)
    }

    @Test
    fun `error with line and column is parsed`() {
        val line = IsBuildOutputParser.parseLine("Error on line 10, column 5 in /tmp/a.iss: oops")
        val p = line as IsBuildOutputParser.Line.Problem
        assertEquals(9, p.line)
        assertEquals(4, p.column)
        assertEquals("/tmp/a.iss", p.file)
    }

    @Test
    fun `warning is flagged`() {
        val line = IsBuildOutputParser.parseLine("Warning on line 3 in a.iss: deprecated")
        val p = line as IsBuildOutputParser.Line.Problem
        assertTrue(p.warning)
        assertEquals(2, p.line)
    }

    @Test
    fun `positionless error has no file or position`() {
        val line = IsBuildOutputParser.parseLine("Error: something went wrong")
        val p = line as IsBuildOutputParser.Line.Problem
        assertNull(p.file)
        assertNull(p.line)
        assertNull(p.column)
        assertEquals("something went wrong", p.message)
        assertEquals("Error: something went wrong", p.detail)
    }

    @Test
    fun `ordinary line is output`() {
        val line = IsBuildOutputParser.parseLine("Compiling setup script...")
        assertTrue(line is IsBuildOutputParser.Line.Output)
        assertEquals("Compiling setup script...", (line as IsBuildOutputParser.Line.Output).text)
    }

    @Test
    fun `line one stays at zero and never goes negative`() {
        val p = IsBuildOutputParser.parseLine("Error on line 1 in a.iss: x") as IsBuildOutputParser.Line.Problem
        assertEquals(0, p.line)
    }
}

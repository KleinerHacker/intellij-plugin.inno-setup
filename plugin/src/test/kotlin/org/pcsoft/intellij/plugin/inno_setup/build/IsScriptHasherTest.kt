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
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class IsScriptHasherTest {

    @get:Rule
    val tmp = TemporaryFolder()

    // ── hashFiles ─────────────────────────────────────────────────────────────

    @Test
    fun `hashFiles is deterministic`() {
        val a = tmp.newFile("a.iss").apply { writeText("[Setup]\n") }
        val b = tmp.newFile("b.iss").apply { writeText("[Files]\n") }
        assertEquals(IsScriptHasher.hashFiles(listOf(a, b)), IsScriptHasher.hashFiles(listOf(a, b)))
    }

    @Test
    fun `hashFiles is independent of order`() {
        val a = tmp.newFile("a.iss").apply { writeText("[Setup]\n") }
        val b = tmp.newFile("b.iss").apply { writeText("[Files]\n") }
        assertEquals(IsScriptHasher.hashFiles(listOf(a, b)), IsScriptHasher.hashFiles(listOf(b, a)))
    }

    @Test
    fun `hashFiles changes when content changes`() {
        val a = tmp.newFile("a.iss").apply { writeText("[Setup]\n") }
        val before = IsScriptHasher.hashFiles(listOf(a))
        a.writeText("[Setup]\nAppName=Demo\n")
        assertNotEquals(before, IsScriptHasher.hashFiles(listOf(a)))
    }

    @Test
    fun `hashFiles tolerates missing files`() {
        val missing = File(tmp.root, "nope.iss")
        // Must not throw; produces a stable digest from the path alone.
        assertEquals(IsScriptHasher.hashFiles(listOf(missing)), IsScriptHasher.hashFiles(listOf(missing)))
    }

    // ── collectParticipatingFiles ─────────────────────────────────────────────

    @Test
    fun `collects the script itself`() {
        val main = tmp.newFile("main.iss").apply { writeText("[Setup]\n") }
        val files = IsScriptHasher.collectParticipatingFiles(main, null)
        assertTrue(files.any { it.name == "main.iss" })
    }

    @Test
    fun `collects nested includes`() {
        val main = tmp.newFile("main.iss").apply { writeText("#include \"part.iss\"\n") }
        tmp.newFile("part.iss").apply { writeText("#include \"deep.iss\"\n") }
        tmp.newFile("deep.iss").apply { writeText("[Files]\n") }
        val names = IsScriptHasher.collectParticipatingFiles(main, null).map { it.name }.toSet()
        assertTrue(names.containsAll(setOf("main.iss", "part.iss", "deep.iss")))
    }

    @Test
    fun `collects referenced local isl files`() {
        val isl = tmp.newFile("German.isl").apply { writeText("[LangOptions]\n") }
        val main = tmp.newFile("main.iss").apply {
            writeText("[Languages]\nName: \"de\"; MessagesFile: \"German.isl\"\n")
        }
        val names = IsScriptHasher.collectParticipatingFiles(main, null).map { it.name }.toSet()
        assertTrue(names.contains("German.isl"))
        assertEquals(isl.canonicalPath, File(tmp.root, "German.isl").canonicalPath)
    }

    @Test
    fun `ignores compiler builtin isl without install path`() {
        val main = tmp.newFile("main.iss").apply {
            writeText("[Languages]\nName: \"en\"; MessagesFile: \"compiler:Default.isl\"\n")
        }
        val names = IsScriptHasher.collectParticipatingFiles(main, null).map { it.name }
        assertFalse(names.any { it.equals("Default.isl", ignoreCase = true) })
    }
}

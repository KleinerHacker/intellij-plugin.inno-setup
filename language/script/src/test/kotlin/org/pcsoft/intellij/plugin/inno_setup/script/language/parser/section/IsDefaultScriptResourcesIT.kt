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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section

import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase
import java.io.File

/**
 * Validates every `*.iss` script under `src/test/resources/default`: each must highlight without any
 * ERROR-level problem. New example scripts dropped into that directory are picked up automatically.
 */
class IsDefaultScriptResourcesIT : IsTimedBasePlatformTestCase() {

    override fun getTestDataPath(): String = TEST_DATA_PATH

    fun testAllDefaultScriptsHaveNoErrors() {
        val scripts = File(TEST_DATA_PATH).walkTopDown().filter { it.isFile && it.extension == "iss" }.toList()
        assertTrue("No *.iss files found under $TEST_DATA_PATH", scripts.isNotEmpty())

        scripts.forEach { script ->
            val relative = script.relativeTo(File(TEST_DATA_PATH)).path.replace('\\', '/')
            val copied = myFixture.copyFileToProject(relative, relative)
            myFixture.configureFromExistingVirtualFile(copied)
            val errors = myFixture.doHighlighting().filter { it.severity.name == "ERROR" }
            assertTrue(
                "$relative must not report errors, but got:\n" +
                        errors.joinToString("\n") { "  [${it.startOffset}-${it.endOffset}] ${it.description}" },
                errors.isEmpty(),
            )
        }
    }

    private companion object {
        const val TEST_DATA_PATH = "src/test/resources/default"
    }
}

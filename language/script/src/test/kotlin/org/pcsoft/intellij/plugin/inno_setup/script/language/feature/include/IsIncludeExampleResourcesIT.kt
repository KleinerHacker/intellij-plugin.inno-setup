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

package org.pcsoft.intellij.plugin.inno_setup.script.language.feature.include

import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Validates the real-world `#include` example scripts under `src/test/resources/include`: each `base.iss`
 * must highlight without ERROR-level problems, because its effective (`#include`-resolved) script is complete.
 */
class IsIncludeExampleResourcesIT : IsTimedBasePlatformTestCase() {

    override fun getTestDataPath(): String = "src/test/resources/include"

    private fun assertBaseHasNoErrors(example: String) {
        myFixture.copyFileToProject("$example/include.iss", "$example/include.iss")
        val base = myFixture.copyFileToProject("$example/base.iss", "$example/base.iss")
        myFixture.configureFromExistingVirtualFile(base)
        val errors = myFixture.doHighlighting().filter { it.severity.name == "ERROR" }
        assertTrue(
            "$example/base.iss must not report errors, but got: " +
                    errors.joinToString("\n") { "[${it.startOffset}-${it.endOffset}] ${it.description}" },
            errors.isEmpty(),
        )
    }

    fun testExample1() = assertBaseHasNoErrors("example1")
    fun testExample2() = assertBaseHasNoErrors("example2")
    fun testExample3() = assertBaseHasNoErrors("example3")
}

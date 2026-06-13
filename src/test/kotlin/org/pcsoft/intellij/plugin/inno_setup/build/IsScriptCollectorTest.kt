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

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Project-level tests for [IsScriptCollector]'s top-level detection and include graph.
 */
class IsScriptCollectorTest : BasePlatformTestCase() {

    fun testTopLevelExcludesIncludedFragment() {
        myFixture.addFileToProject("main.iss", "[Setup]\n#include \"part.iss\"\n")
        myFixture.addFileToProject("part.iss", "[Files]\n")

        val collector = IsScriptCollector(project)
        val topLevel = collector.collectTopLevel().map { it.name }.toSet()

        assertTrue("main.iss must be top-level", "main.iss" in topLevel)
        assertFalse("part.iss is included and must be excluded", "part.iss" in topLevel)
    }

    fun testIsIncludedByOther() {
        val main = myFixture.addFileToProject("main.iss", "#include \"part.iss\"\n")
        val part = myFixture.addFileToProject("part.iss", "[Files]\n")

        val collector = IsScriptCollector(project)
        assertTrue(collector.isIncludedByOther(part.virtualFile))
        assertFalse(collector.isIncludedByOther(main.virtualFile))
    }

    fun testStandaloneScriptIsTopLevel() {
        val solo = myFixture.addFileToProject("solo.iss", "[Setup]\nAppName=x\n")
        val collector = IsScriptCollector(project)
        assertTrue("solo.iss" in collector.collectTopLevel().map { it.name })
        assertFalse(collector.isIncludedByOther(solo.virtualFile))
    }
}

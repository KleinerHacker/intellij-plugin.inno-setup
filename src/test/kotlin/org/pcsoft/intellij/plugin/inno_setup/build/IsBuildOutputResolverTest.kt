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
 * Tests [IsBuildOutputResolver]'s end-to-end resolution, including reading `[Setup] OutputDir` via PSI.
 */
class IsBuildOutputResolverTest : BasePlatformTestCase() {

    fun testBuildDirMapsRelativeOutputDir() {
        val file = myFixture.addFileToProject("setup.iss", "[Setup]\nOutputDir=Release\n")
        val arg = IsBuildOutputResolver(project).resolveOutputArg(file.virtualFile, IsBuildOutputMode.BUILD_DIR)
        assertNotNull(arg)
        assertTrue(arg!!.startsWith("/O"))
        assertTrue("must end with the relative dir, was: $arg", arg.endsWith("Release"))
    }

    fun testBuildDirDefaultsToOutputWhenMissing() {
        val file = myFixture.addFileToProject("setup.iss", "[Setup]\nAppName=x\n")
        val arg = IsBuildOutputResolver(project).resolveOutputArg(file.virtualFile, IsBuildOutputMode.BUILD_DIR)
        assertTrue(arg!!.endsWith("Output"))
    }

    fun testBuildDirLeavesAbsoluteUntouched() {
        val abs = if (System.getProperty("os.name").startsWith("Windows")) "C:\\dist" else "/dist"
        val file = myFixture.addFileToProject("setup.iss", "[Setup]\nOutputDir=$abs\n")
        assertNull(IsBuildOutputResolver(project).resolveOutputArg(file.virtualFile, IsBuildOutputMode.BUILD_DIR))
    }

    fun testScriptModeReturnsNull() {
        val file = myFixture.addFileToProject("setup.iss", "[Setup]\nOutputDir=Release\n")
        assertNull(IsBuildOutputResolver(project).resolveOutputArg(file.virtualFile, IsBuildOutputMode.SCRIPT))
    }

    fun testDryModeDisablesOutput() {
        val file = myFixture.addFileToProject("setup.iss", "[Setup]\nOutputDir=Release\n")
        assertEquals("/O-", IsBuildOutputResolver(project).resolveOutputArg(file.virtualFile, IsBuildOutputMode.DRY))
    }
}

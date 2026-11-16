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

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests [IsBuildOutputResolver]'s end-to-end resolution, including reading `\[Setup] OutputDir` via PSI.
 */
class IsBuildOutputResolverTest : IsTimedBasePlatformTestCase() {

    fun testBuildDirMapsRelativeOutputDir() {
        val file = myFixture.addFileToProject("setup.iss", "[Setup]\nOutputDir=Release\n")
        val arg = IsBuildOutputResolver(project).resolveOutputArg(file.virtualFile, IsBuildOutputMode.BUILD_DIR)
        assertNotNull(arg)
        assertTrue("must start with an unquoted /O, was: $arg", arg!!.startsWith("/O") && !arg.contains('"'))
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

    /**
     * Issue #60: a relative `OutputDir` given as `{#Define}` is expanded, so the `/O` argument ends with the
     * define's value and never contains the unexpanded `{#…}` text.
     */
    fun testBuildDirExpandsDefineInOutputDir() {
        val file = myFixture.addFileToProject(
            "setup.iss",
            "#define OutputDirectoryPath \"artifacts\\setup\"\n[Setup]\nOutputDir={#OutputDirectoryPath}\n"
        )
        val arg = IsBuildOutputResolver(project).resolveOutputArg(file.virtualFile, IsBuildOutputMode.BUILD_DIR)
        assertNotNull(arg)
        assertFalse("must not contain the unexpanded macro, was: $arg", arg!!.contains("{#"))
        assertTrue("must end with the define value, was: $arg", arg.endsWith("artifacts\\setup"))
    }

    /**
     * Issue #60: an `OutputDir` that cannot be expanded statically must not produce an `/O` override, so the
     * compiler expands the value itself.
     */
    fun testBuildDirDoesNotOverrideUnresolvableOutputDir() {
        val file = myFixture.addFileToProject("setup.iss", "[Setup]\nOutputDir={#Missing}\n")
        assertNull(IsBuildOutputResolver(project).resolveOutputArg(file.virtualFile, IsBuildOutputMode.BUILD_DIR))
    }

    /**
     * The scanned result distinguishes a missing `OutputDir` from one that is present but not expandable.
     */
    fun testResolveScriptOutputDirDistinguishesAbsentResolvedUnresolved() {
        val resolver = IsBuildOutputResolver(project)
        val absent = myFixture.addFileToProject("a.iss", "[Setup]\nAppName=x\n")
        val resolved = myFixture.addFileToProject("b.iss", "#define D \"dist\"\n[Setup]\nOutputDir={#D}\n")
        val unresolved = myFixture.addFileToProject("c.iss", "[Setup]\nOutputDir={#Nope}\n")
        assertEquals(IsScriptOutputDir.Absent, resolver.resolveScriptOutputDir(absent.virtualFile))
        assertEquals(IsScriptOutputDir.Resolved("dist"), resolver.resolveScriptOutputDir(resolved.virtualFile))
        assertEquals(IsScriptOutputDir.Unresolved, resolver.resolveScriptOutputDir(unresolved.virtualFile))
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

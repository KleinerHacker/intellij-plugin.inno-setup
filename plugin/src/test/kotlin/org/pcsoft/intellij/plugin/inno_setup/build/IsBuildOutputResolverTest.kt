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

    /**
     * An `OutputDir` that lives in an `#include`d file is found: the main script has none of its own, the
     * included `\[Setup]` section does.
     */
    fun testOutputDirInIncludeIsFound() {
        myFixture.addFileToProject("inc.iss", "[Setup]\nOutputDir=Release\n")
        val main = myFixture.addFileToProject("main.iss", "#include \"inc.iss\"\n[Setup]\nAppName=x\n")
        val arg = IsBuildOutputResolver(project).resolveOutputArg(main.virtualFile, IsBuildOutputMode.BUILD_DIR)
        assertNotNull(arg)
        assertTrue("must end with the included OutputDir, was: $arg", arg!!.endsWith("Release"))
    }

    /**
     * A define from the main script is visible to an `OutputDir` that is pulled in from an `#include`.
     */
    fun testIncludeOutputDirExpandsDefineOfMainScript() {
        myFixture.addFileToProject("inc.iss", "[Setup]\nOutputDir={#D}\n")
        val main = myFixture.addFileToProject("main.iss", "#define D \"dist\"\n#include \"inc.iss\"\n")
        assertEquals(
            IsScriptOutputDir.Resolved("dist"),
            IsBuildOutputResolver(project).resolveScriptOutputDir(main.virtualFile)
        )
    }

    /**
     * A define from an `#include`d file is visible to an `OutputDir` in the main script.
     */
    fun testOutputDirExpandsDefineOfInclude() {
        myFixture.addFileToProject("inc.iss", "#define D \"dist\"\n")
        val main = myFixture.addFileToProject("main.iss", "#include \"inc.iss\"\n[Setup]\nOutputDir={#D}\n")
        assertEquals(
            IsScriptOutputDir.Resolved("dist"),
            IsBuildOutputResolver(project).resolveScriptOutputDir(main.virtualFile)
        )
    }

    /**
     * `{src}` always means the directory of the main script, also when the `OutputDir` comes from an `#include`
     * in a sub-folder, so it is expanded and never left as literal text.
     */
    fun testSrcConstantInIncludeIsExpanded() {
        myFixture.addFileToProject("sub/inc.iss", "[Setup]\nOutputDir={src}\\dist\n")
        val main = myFixture.addFileToProject("main.iss", "#include \"sub\\inc.iss\"\n[Setup]\nAppName=x\n")
        val result = IsBuildOutputResolver(project).resolveScriptOutputDir(main.virtualFile)
        assertTrue("must be resolved, was: $result", result is IsScriptOutputDir.Resolved)
        val path = (result as IsScriptOutputDir.Resolved).path
        assertTrue("must not keep the constant, was: $path", !path.contains("{") && path.endsWith("dist"))
    }

    /**
     * `{#SourcePath}` is the directory of the file being processed. In the main script it is the script's
     * directory and therefore expandable.
     */
    fun testSourcePathInMainScriptIsExpanded() {
        val main = myFixture.addFileToProject("main.iss", "[Setup]\nOutputDir={#SourcePath}\\dist\n")
        val result = IsBuildOutputResolver(project).resolveScriptOutputDir(main.virtualFile)
        assertTrue("must be resolved, was: $result", result is IsScriptOutputDir.Resolved)
        assertTrue((result as IsScriptOutputDir.Resolved).path.endsWith("dist"))
    }

    /**
     * In an `#include`d file in a sub-folder `{#SourcePath}` is that sub-folder, not the main script's
     * directory. The plugin cannot expand it correctly, so the value is reported as unresolved.
     */
    fun testSourcePathInIncludeIsUnresolved() {
        myFixture.addFileToProject("sub/inc.iss", "[Setup]\nOutputDir={#SourcePath}\\dist\n")
        val main = myFixture.addFileToProject("main.iss", "#include \"sub\\inc.iss\"\n[Setup]\nAppName=x\n")
        assertEquals(
            IsScriptOutputDir.Unresolved,
            IsBuildOutputResolver(project).resolveScriptOutputDir(main.virtualFile)
        )
    }

    /**
     * An environment variable that is set is expanded, one that is not set (and has no default) makes the value
     * unresolved.
     */
    fun testEnvironmentVariableInOutputDir() {
        val resolver = IsBuildOutputResolver(project)
        val set = myFixture.addFileToProject("a.iss", "[Setup]\nOutputDir={%PATH}\n")
        val unset = myFixture.addFileToProject("b.iss", "[Setup]\nOutputDir={%INNO_UNSET_VARIABLE_FOR_TEST}\n")
        assertTrue(resolver.resolveScriptOutputDir(set.virtualFile) is IsScriptOutputDir.Resolved)
        assertEquals(IsScriptOutputDir.Unresolved, resolver.resolveScriptOutputDir(unset.virtualFile))
    }

    /**
     * An `#include` that points to a missing file contributes nothing, so the script keeps having no
     * `OutputDir`.
     */
    fun testMissingIncludeLeavesOutputDirAbsent() {
        val main = myFixture.addFileToProject("main.iss", "#include \"nope.iss\"\n[Setup]\nAppName=x\n")
        assertEquals(
            IsScriptOutputDir.Absent,
            IsBuildOutputResolver(project).resolveScriptOutputDir(main.virtualFile)
        )
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

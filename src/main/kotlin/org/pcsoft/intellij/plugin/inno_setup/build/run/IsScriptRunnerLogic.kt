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

package org.pcsoft.intellij.plugin.inno_setup.build.run

import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode
import java.io.File
import java.util.UUID

/**
 * Pure, dependency-free helpers for the run pipeline. All methods are static-like companions or
 * top-level so they can be unit-tested without an IntelliJ project.
 */
object IsScriptRunnerLogic {

    /**
     * Returns the ISCC `/O` argument to use when running (not just building): always produces a
     * `.exe`. If the project is configured for [IsBuildOutputMode.DRY] (which suppresses output),
     * a temporary directory is used instead so the installer can be launched.
     *
     * @param projectOutputMode the mode currently saved in project settings
     * @param buildRoot         the project build directory (used for [IsBuildOutputMode.BUILD_DIR])
     * @param tempBase          the temp directory root (injectable for tests)
     */
    fun buildOutputArg(
        projectOutputMode: IsBuildOutputMode,
        buildRoot: File,
        scriptOutputDir: String?,
        tempBase: File = File(System.getProperty("java.io.tmpdir"))
    ): String = when (projectOutputMode) {
        IsBuildOutputMode.DRY -> "/O" + File(tempBase, "inno-run-${UUID.randomUUID()}").path
        IsBuildOutputMode.SCRIPT -> "/O" + (scriptOutputDir?.let { if (File(it).isAbsolute) it else File(buildRoot, it).path } ?: File(buildRoot, "Output").path)
        IsBuildOutputMode.BUILD_DIR -> "/O" + File(buildRoot, scriptOutputDir?.takeIf { !File(it).isAbsolute } ?: "Output").path
    }

    /**
     * Builds the command-line arguments list for launching the compiled installer.
     * Returns a list starting with the exe path, followed by flags.
     */
    fun buildInstallerArgs(
        exeFile: File,
        runMode: IsRunMode,
        languageOverride: String?,
        debugOutput: Boolean,
        logFile: File?
    ): List<String> {
        val args = mutableListOf(exeFile.absolutePath)
        when (runMode) {
            IsRunMode.DRY -> {
                args += "/VERYSILENT"
                args += "/SUPPRESSMSGBOXES"
                args += "/NORESTART"
            }
            IsRunMode.REAL -> { /* no extra flags — show UI */ }
        }
        if (debugOutput && logFile != null) args += "/LOG=${logFile.absolutePath}"
        languageOverride?.takeIf { it.isNotBlank() }?.let { args += "/LANG=$it" }
        return args
    }

    /**
     * Finds the primary setup executable (not `unins*.exe`) inside [outputDir].
     */
    fun findSetupExe(outputDir: File): File? =
        outputDir.listFiles { f ->
            f.isFile &&
            f.extension.equals("exe", ignoreCase = true) &&
            !f.name.startsWith("unins", ignoreCase = true)
        }?.firstOrNull()

    /**
     * Finds the uninstaller executable (`unins*.exe`) inside [uninstallerDir].
     */
    fun findUninstallerExe(uninstallerDir: File): File? =
        uninstallerDir.listFiles { f ->
            f.isFile &&
            f.extension.equals("exe", ignoreCase = true) &&
            f.name.startsWith("unins", ignoreCase = true)
        }?.firstOrNull()

    /**
     * Extracts the output directory from the `/O…` argument produced by [buildOutputArg].
     */
    fun outputDirFromArg(outputArg: String): File? {
        if (!outputArg.startsWith("/O", ignoreCase = true)) return null
        val path = outputArg.substring(2)
        return if (path.isNotBlank()) File(path) else null
    }
}

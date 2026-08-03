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
import java.util.*

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
     * @param persistentTempDir an existing temp directory to reuse in [IsBuildOutputMode.DRY]; when
     *                          `null` or blank, a fresh one is generated (the caller is expected to
     *                          persist the generated path on the run configuration).
     * @param tempBase          the temp directory root (injectable for tests)
     */
    fun buildOutputArg(
        projectOutputMode: IsBuildOutputMode,
        buildRoot: File,
        scriptOutputDir: String?,
        persistentTempDir: String? = null,
        tempBase: File = File(System.getProperty("java.io.tmpdir"))
    ): String = when (projectOutputMode) {
        IsBuildOutputMode.DRY -> outputArg(persistentTempDir?.takeIf { it.isNotBlank() }
            ?: File(tempBase, "inno-run-${UUID.randomUUID()}").path)

        IsBuildOutputMode.SCRIPT -> outputArg(scriptOutputDir?.let {
            if (File(it).isAbsolute) it else File(
                buildRoot,
                it
            ).path
        } ?: File(buildRoot, "Output").path)

        IsBuildOutputMode.BUILD_DIR -> outputArg(
            File(
                buildRoot,
                scriptOutputDir?.takeIf { !File(it).isAbsolute } ?: "Output").path)
    }

    /** Formats an ISCC `/O` argument; the path is wrapped in double quotes as ISCC expects. */
    fun outputArg(path: String): String = "/O\"$path\""

    /**
     * Applies a build configuration's output directory override to an already resolved [baseArg].
     *
     * A blank [outputDirOverride] keeps [baseArg] untouched — the override is opt-in, so a configuration
     * that only defines symbols still follows the project's output rule. A relative override is resolved
     * against [buildRoot] so `dist/debug` lands inside the project's build folder rather than the working
     * directory ISCC happens to run in.
     */
    fun applyOutputOverride(baseArg: String?, outputDirOverride: String?, buildRoot: File): String? {
        val override = outputDirOverride?.trim()?.removeSurrounding("\"")?.takeIf { it.isNotEmpty() }
            ?: return baseArg
        val resolved = if (File(override).isAbsolute) override else File(buildRoot, override).path
        return outputArg(resolved)
    }

    /**
     * Builds the command-line arguments list for launching the compiled installer.
     * Returns a list starting with the exe path, followed by flags.
     */
    fun buildInstallerArgs(
        exeFile: File,
        languageOverride: String?,
        debugOutput: Boolean,
        logFile: File?
    ): List<String> {
        val args = mutableListOf(exeFile.absolutePath)
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
     * Extracts the output directory from the `/O…` argument produced by [buildOutputArg].
     */
    fun outputDirFromArg(outputArg: String): File? {
        if (!outputArg.startsWith("/O", ignoreCase = true)) return null
        val path = outputArg.substring(2).removeSurrounding("\"")
        return if (path.isNotBlank()) File(path) else null
    }
}

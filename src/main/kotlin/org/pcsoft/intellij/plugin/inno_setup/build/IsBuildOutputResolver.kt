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

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputResolver.Companion.computeOutputArg
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputResolver.Companion.detectBuildSubdir
import java.io.File

/**
 * Determines the ISCC `/O` argument for a script according to the configured [IsBuildOutputMode].
 *
 * The path-mapping decisions live in pure, dependency-free helpers ([detectBuildSubdir],
 * [computeOutputArg]) so they can be unit-tested without a project; only [resolveOutputArg] reaches
 * into PSI to read the script's `[Setup] OutputDir`.
 */
class IsBuildOutputResolver(private val project: Project) {

    /** ISCC's default output directory when a script does not specify `OutputDir`. */
    private val defaultOutputDir = "Output"

    /**
     * Resolves the `/O…` command-line argument for [scriptFile], or `null` when ISCC's own output
     * handling should be left untouched (mode [IsBuildOutputMode.SCRIPT], or an absolute
     * `OutputDir` in [IsBuildOutputMode.BUILD_DIR]).
     */
    fun resolveOutputArg(scriptFile: VirtualFile, mode: IsBuildOutputMode): String? {
        val scriptOutputDir = when (mode) {
            IsBuildOutputMode.BUILD_DIR -> readScriptOutputDir(scriptFile) ?: defaultOutputDir
            else -> null
        }
        return computeOutputArg(mode, scriptOutputDir, buildRoot())
    }

    /**
     * Returns the project's build directory (`<basePath>/<out|target|build>`).
     */
    fun buildRoot(): File {
        val base = File(project.basePath ?: ".")
        return File(base, detectBuildSubdir(base))
    }

    private fun readScriptOutputDir(scriptFile: VirtualFile): String? {
        val text = runCatching { VfsUtilCore.loadText(scriptFile) }.getOrNull() ?: return null
        return parseOutputDir(text)
    }

    companion object {
        // Matches an "OutputDir=..." directive anywhere in the script text. Read from raw text rather
        // than PSI because the section lexer splits values on ':' and would truncate Windows paths.
        private val OUTPUT_DIR = Regex("""^\s*OutputDir\s*=\s*(.+?)\s*$""", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

        /**
         * Extracts the `[Setup] OutputDir` value from raw script [text], or `null` when absent.
         */
        fun parseOutputDir(text: String): String? =
            OUTPUT_DIR.find(text)?.groupValues?.get(1)?.trim()?.removeSurrounding("\"")?.takeIf { it.isNotBlank() }

        /**
         * Picks the conventional build sub-directory for a project rooted at [baseDir]: `target`
         * for Maven, `build` for Gradle, otherwise IntelliJ's `out`.
         */
        fun detectBuildSubdir(baseDir: File): String = when {
            File(baseDir, "pom.xml").exists() -> "target"
            File(baseDir, "build.gradle").exists() || File(baseDir, "build.gradle.kts").exists() -> "build"
            else -> "out"
        }

        /**
         * Pure mapping of a mode + the script's `OutputDir` to the ISCC `/O` argument.
         *
         * - [IsBuildOutputMode.DRY] → `/O-` (disable output)
         * - [IsBuildOutputMode.SCRIPT] → `null` (no override)
         * - [IsBuildOutputMode.BUILD_DIR] → `/O<buildRoot>/<relative>` for a relative
         *   [scriptOutputDir], or `null` when it is absolute (leave it untouched).
         */
        fun computeOutputArg(mode: IsBuildOutputMode, scriptOutputDir: String?, buildRoot: File): String? =
            when (mode) {
                IsBuildOutputMode.DRY -> "/O-"
                IsBuildOutputMode.SCRIPT -> null
                IsBuildOutputMode.BUILD_DIR -> {
                    val relative = scriptOutputDir ?: "Output"
                    // ISCC expects the path after /O wrapped in double quotes.
                    if (File(relative).isAbsolute) null
                    else "/O\"" + File(buildRoot, relative).path + "\""
                }
            }
    }
}

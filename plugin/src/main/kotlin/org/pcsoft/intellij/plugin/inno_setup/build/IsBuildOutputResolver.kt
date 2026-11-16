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

import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.IsMessagesFileResolver
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.include.declarationScope
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import java.io.File

/**
 * Outcome of reading a script's `\[Setup] OutputDir` and expanding its preprocessor defines and constants.
 */
sealed interface IsScriptOutputDir {

    /** The script does not define an `OutputDir`. */
    object Absent : IsScriptOutputDir

    /** The `OutputDir` was fully expanded to [path] (which may still be relative). */
    data class Resolved(val path: String) : IsScriptOutputDir

    /**
     * The `OutputDir` contains a macro or constant that cannot be expanded statically (for example a
     * `{#Define}` computed by an expression). Only the Inno Setup compiler can tell where the output goes.
     */
    object Unresolved : IsScriptOutputDir
}

/**
 * Determines the ISCC `/O` argument for a script according to the configured [IsBuildOutputMode].
 *
 * The path-mapping decisions live in pure, dependency-free helpers ([detectBuildSubdir],
 * [computeOutputArg], [expandOutputDir]) so they can be unit-tested without a project; only
 * [resolveOutputArg] reaches into PSI to read the script's `\[Setup] OutputDir`.
 */
class IsBuildOutputResolver(private val project: Project) {

    /** ISCC's default output directory when a script does not specify `OutputDir`. */
    private val defaultOutputDir = "Output"

    /**
     * Resolves the `/O…` command-line argument for [scriptFile], or `null` when ISCC's own output
     * handling should be left untouched (mode [IsBuildOutputMode.SCRIPT], an absolute `OutputDir` in
     * [IsBuildOutputMode.BUILD_DIR], or an `OutputDir` that cannot be expanded statically — the compiler
     * then expands it itself).
     */
    fun resolveOutputArg(scriptFile: VirtualFile, mode: IsBuildOutputMode): String? {
        val scriptOutputDir = when (mode) {
            IsBuildOutputMode.BUILD_DIR -> when (val dir = resolveScriptOutputDir(scriptFile)) {
                is IsScriptOutputDir.Resolved -> dir.path
                IsScriptOutputDir.Absent -> defaultOutputDir
                IsScriptOutputDir.Unresolved -> return null
            }

            else -> null
        }
        return computeOutputArg(mode, scriptOutputDir, buildRoot())
    }

    /**
     * Reads the script's `\[Setup] OutputDir` and expands its `{#Define}`, `{%ENV}` and built-in constants
     * against the `#define`s of the effective (`#include`-resolved) script.
     */
    fun resolveScriptOutputDir(scriptFile: VirtualFile): IsScriptOutputDir {
        val raw = readScriptOutputDir(scriptFile) ?: return IsScriptOutputDir.Absent
        val scriptDir = scriptFile.parent?.path?.let { File(it) }
        val defines = runReadActionBlocking {
            (PsiManager.getInstance(project).findFile(scriptFile) as? IsScriptFile)
                ?.declarationScope()?.definedConstants
        }.orEmpty()
        val installPath = IsSettingsService.getInstance().state.installationPath
        return expandOutputDir(raw, defines, scriptDir, installPath)
            ?.let { IsScriptOutputDir.Resolved(it) }
            ?: IsScriptOutputDir.Unresolved
    }

    /**
     * The expanded `OutputDir` of [scriptFile] for the run pipeline, or `null` when the script defines none or
     * it cannot be expanded statically. A run needs a known directory to find the produced installer, so the
     * caller falls back to its own output directory in that case.
     */
    fun resolveRunScriptOutputDir(scriptFile: File): String? {
        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(scriptFile) ?: return null
        return (resolveScriptOutputDir(virtualFile) as? IsScriptOutputDir.Resolved)?.path
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
        private val OUTPUT_DIR =
            Regex("""^\s*OutputDir\s*=\s*(.+?)\s*$""", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

        /**
         * Extracts the `\[Setup] OutputDir` value from raw script [text], or `null` when absent.
         */
        fun parseOutputDir(text: String): String? =
            OUTPUT_DIR.find(text)?.groupValues?.get(1)?.trim()?.removeSurrounding("\"")?.takeIf { it.isNotBlank() }

        /**
         * Expands the placeholders of a raw `OutputDir` value ([raw]) using [defines] (`#define` name/value
         * pairs), [scriptDir] and [installPath]. Returns `null` when any placeholder cannot be expanded
         * statically, or when the result is blank.
         */
        fun expandOutputDir(
            raw: String,
            defines: List<Pair<String, String?>>,
            scriptDir: File?,
            installPath: String?
        ): String? =
            IsMessagesFileResolver.expandValue(raw, defines, scriptDir, emptyMap(), installPath)
                ?.trim()?.takeIf { it.isNotEmpty() }

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
                    // Unquoted on purpose: the argument goes to GeneralCommandLine as a single parameter,
                    // which quotes it for Windows itself. See IsScriptRunnerLogic.outputArg.
                    if (File(relative).isAbsolute) null
                    else "/O" + File(buildRoot, relative).path
                }
            }
    }
}

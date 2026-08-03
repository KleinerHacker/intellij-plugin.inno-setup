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

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.task.ProjectTaskRunner
import com.intellij.task.TaskRunnerResults
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsScriptRunnerLogic
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.build.IsScriptCollector
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import org.pcsoft.intellij.plugin.inno_setup.settings.IsBuildSettingsService
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsConfigurable
import java.io.File

/**
 * Compiles Inno Setup scripts with ISCC and streams the output into the dedicated "Inno Setup" tool window
 * console ([IsBuildConsoleService]), reporting the final result as a balloon notification. Shared by the
 * project-build hook ([compileProject]) and the per-file context-menu action ([compileScript]).
 *
 * Uses only public platform APIs (console view, notifications) — deliberately not the internal Build
 * tool-window/event APIs.
 */
@Service(Service.Level.PROJECT)
class IsCompilerService(private val project: Project) {

    private val group = IS_BUILD_TOOL_WINDOW_ID

    /**
     * Compiles all top-level scripts in the project. Blocking — intended to be called off the EDT
     * from the build pipeline. Returns the aggregate task-runner result.
     */
    @JvmOverloads
    fun compileProject(force: Boolean = false): ProjectTaskRunner.Result {
        val scripts = IsScriptCollector(project).collectTopLevel()
        return runCompilation(scripts, PluginBundle.message("build.title.project"), force)
    }

    /**
     * Compiles a single [scriptFile] on a pooled thread (used by the context-menu action), optionally with
     * the options of [buildConfig]. An explicit single-file action always forces a (re)build.
     */
    @JvmOverloads
    fun compileScript(scriptFile: VirtualFile, buildConfig: IsBuildConfiguration? = null) {
        ApplicationManager.getApplication().executeOnPooledThread {
            runCompilation(
                listOf(scriptFile),
                PluginBundle.message("build.title.script", scriptFile.name),
                force = true,
                buildConfig = buildConfig
            )
        }
    }

    /**
     * Returns the configured `ISCC.exe`, or `null` when no valid installation directory is set.
     */
    fun isccExecutable(): File? {
        val path = IsSettingsService.getInstance().state.installationPath
        if (path.isNullOrBlank()) return null
        val exe = File(path, "ISCC.exe")
        return if (exe.isFile) exe else null
    }

    private fun outputMode(): IsBuildOutputMode =
        IsBuildOutputMode.fromName(IsBuildSettingsService.getInstance(project).state.outputMode)

    /**
     * Compiles a single [scriptFile] for the run pipeline
     * ([org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunProcessHandler]) using an explicit
     * [outputArg] — a run always needs a real installer, even when the project is configured
     * [IsBuildOutputMode.DRY] (which on its own suppresses output). The rebuild decision stays with
     * the build: the compile is skipped only when the participating files and the options of
     * [buildConfig] are unchanged since the last successful build to the same output *and*
     * [hasArtifact] reports the produced installer is still present. Blocking — intended to be called
     * off the EDT. Streams to the build console and returns the aggregate task-runner result.
     */
    fun compileScriptForRun(
        scriptFile: VirtualFile,
        outputArg: String?,
        hasArtifact: Boolean,
        force: Boolean = false,
        buildConfig: IsBuildConfiguration? = null
    ): ProjectTaskRunner.Result =
        runCompilation(
            listOf(scriptFile),
            PluginBundle.message("build.title.script", scriptFile.name),
            force,
            outputArgFor = { outputArg },
            artifactPresent = { hasArtifact },
            buildConfig = buildConfig
        )

    /**
     * @param outputArgFor     overrides the project-mode `/O…` resolution per script (used by the run
     *                         pipeline); `null` falls back to the configured [outputMode].
     * @param artifactPresent  extra up-to-date guard: a script is only skipped when its build artifact
     *                         still exists. Defaults to always-present for the regular build.
     * @param buildConfig      the selected build configuration, contributing `/D…` symbols, an output
     *                         directory override and extra ISCC options; the regular project build uses
     *                         none.
     */
    private fun runCompilation(
        scripts: List<VirtualFile>,
        title: String,
        force: Boolean,
        outputArgFor: ((VirtualFile) -> String?)? = null,
        artifactPresent: (VirtualFile) -> Boolean = { true },
        buildConfig: IsBuildConfiguration? = null
    ): ProjectTaskRunner.Result {
        // Flush unsaved editor changes so ISCC compiles the current content from disk.
        ApplicationManager.getApplication().invokeAndWait {
            FileDocumentManager.getInstance().saveAllDocuments()
        }

        val consoleService = project.service<IsBuildConsoleService>()
        val console = consoleService.start()
        consoleService.print(console, "$group: $title\n", error = false)

        var hasErrors = false
        var firstError: ErrorLocation? = null

        val iscc = isccExecutable()
        if (iscc == null) {
            consoleService.print(console, PluginBundle.message("build.not_configured.detail") + "\n", error = true)
            notify(PluginBundle.message("build.not_configured.title"), NotificationType.ERROR)
            openSettings()
            return TaskRunnerResults.FAILURE
        }

        if (scripts.isEmpty()) {
            consoleService.print(console, PluginBundle.message("build.no_scripts") + "\n", error = false)
        }

        val resolver = IsBuildOutputResolver(project)
        val mode = outputMode()
        val installPath = IsSettingsService.getInstance().state.installationPath
        val buildHashes = IsBuildSettingsService.getInstance(project).state.buildHashes
        val defines = buildConfig?.isccDefines ?: emptyList()
        val extraOptions = buildConfig?.isccOptions ?: emptyList()

        if (buildConfig != null) {
            consoleService.print(
                console,
                PluginBundle.message("build.using_configuration", buildConfig.name) + "\n",
                error = false
            )
        }

        for (script in scripts) {
            val baseOutputArg = outputArgFor?.invoke(script) ?: resolver.resolveOutputArg(script, mode)
            val outputArg = IsScriptRunnerLogic.applyOutputOverride(
                baseOutputArg, buildConfig?.outputDirOverride, resolver.buildRoot()
            )
            // Skip scripts whose participating files are unchanged since the last successful build to
            // the same output, unless a rebuild was forced or the previously produced artifact is gone.
            val scriptKey = hashKey(script.path, outputArg, buildConfig)
            val currentHash = IsScriptHasher.hashScript(File(script.path), installPath)
            if (!force && buildHashes[scriptKey] == currentHash && artifactPresent(script)) {
                consoleService.print(
                    console,
                    PluginBundle.message("build.up_to_date", script.name) + "\n",
                    error = false
                )
                continue
            }

            consoleService.print(console, PluginBundle.message("build.compiling", script.name) + "\n", error = false)
            val commandLine =
                buildCommandLine(iscc, script.path, script.parent?.path, outputArg, defines, extraOptions)

            try {
                val handler = OSProcessHandler(commandLine)
                handler.addProcessListener(object : ProcessListener {
                    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                        val stdout = outputType === ProcessOutputTypes.STDOUT
                        for (rawLine in event.text.split('\n')) {
                            if (rawLine.isBlank()) continue
                            when (val parsed = IsBuildOutputParser.parseLine(rawLine)) {
                                is IsBuildOutputParser.Line.Output ->
                                    consoleService.print(console, parsed.text + "\n", error = !stdout)

                                is IsBuildOutputParser.Line.Problem -> {
                                    if (!parsed.warning) hasErrors = true
                                    // Print the raw problem line; IsBuildOutputFilter turns its "line XX"
                                    // token into a navigable hyperlink.
                                    consoleService.print(console, parsed.detail + "\n", error = !parsed.warning)
                                    val vFile = parsed.file?.let { resolvePath(it) }
                                    if (!parsed.warning && firstError == null && vFile != null && parsed.line != null) {
                                        firstError = ErrorLocation(vFile, parsed.line, parsed.column ?: 0)
                                    }
                                }
                            }
                        }
                    }
                })
                handler.startNotify()
                handler.waitFor()
                if (handler.exitCode != 0) hasErrors = true
                else buildHashes[scriptKey] = currentHash
            } catch (e: Exception) {
                thisLogger().warn("Failed to run ISCC for ${script.path}", e)
                hasErrors = true
                consoleService.print(
                    console,
                    PluginBundle.message("build.run_failed", e.message ?: "") + "\n",
                    error = true
                )
            }
        }

        firstError?.let { navigateTo(it) }

        if (hasErrors) {
            consoleService.print(console, PluginBundle.message("build.finish.failed") + "\n", error = true)
            notify(PluginBundle.message("build.finish.failed"), NotificationType.ERROR)
        } else {
            consoleService.print(console, PluginBundle.message("build.finish.success") + "\n", error = false)
            notify(PluginBundle.message("build.finish.success"), NotificationType.INFORMATION)
        }
        return if (hasErrors) TaskRunnerResults.FAILURE else TaskRunnerResults.SUCCESS
    }

    /** A navigable error position in a source file (0-based line/column). */
    private data class ErrorLocation(val file: VirtualFile, val line: Int, val column: Int)

    /** Shows a balloon notification in the "Inno Setup" group (no-op in headless/unit-test mode). */
    private fun notify(message: String, type: NotificationType) {
        val application = ApplicationManager.getApplication()
        if (application.isUnitTestMode || application.isHeadlessEnvironment) return
        application.invokeLater {
            if (project.isDisposed) return@invokeLater
            NotificationGroupManager.getInstance()
                .getNotificationGroup(group)
                .createNotification(message, type)
                .notify(project)
        }
    }

    private fun openSettings() {
        val application = ApplicationManager.getApplication()
        if (application.isUnitTestMode || application.isHeadlessEnvironment) return
        application.invokeLater {
            if (project.isDisposed) return@invokeLater
            ShowSettingsUtil.getInstance()
                .showSettingsDialog(project, IsSettingsConfigurable::class.java)
        }
    }

    companion object {

        private fun canonicalPath(path: String): String =
            runCatching { File(path).canonicalPath }.getOrDefault(File(path).absolutePath)

        /**
         * Identity of one build: same script, same output *and* the same build configuration content.
         *
         * The build configuration enters by its [IsBuildConfiguration.contentHash] rather than its name,
         * because what decides whether the previous installer is still valid is what was compiled, not what
         * it was called. Editing `Debug`'s symbols therefore invalidates it, while renaming `Debug` does not
         * — and a run switched from `Debug` to `Release` never reuses the other one's artifact.
         */
        fun hashKey(path: String, outputArg: String?, buildConfig: IsBuildConfiguration?): String =
            canonicalPath(path) + '|' + (outputArg ?: "") + '|' + (buildConfig?.contentHash() ?: "")

        /**
         * Builds the ISCC command line. Pure with respect to IntelliJ project state so it can be
         * unit-tested: the script path, optional working directory, optional `/O…` argument, the `/D…`
         * symbols and any extra options are assembled into a [GeneralCommandLine].
         */
        @JvmOverloads
        fun buildCommandLine(
            iscc: File,
            scriptPath: String,
            workDir: String?,
            outputArg: String?,
            defines: List<String> = emptyList(),
            extraOptions: List<String> = emptyList()
        ): GeneralCommandLine {
            val cmd = GeneralCommandLine(iscc.absolutePath)
            outputArg?.let { cmd.addParameter(it) }
            // /D must precede the script name, like every other ISCC option.
            defines.forEach { cmd.addParameter(it) }
            // Extra options come last among the options so an explicit one can still override the rest.
            extraOptions.forEach { cmd.addParameter(it) }
            cmd.addParameter(scriptPath)
            if (workDir != null) cmd.withWorkDirectory(workDir)
            return cmd
        }
    }

    private fun resolvePath(path: String): VirtualFile? =
        LocalFileSystem.getInstance().findFileByPath(path.replace('\\', '/'))

    private fun navigateTo(location: ErrorLocation) {
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed || !location.file.isValid) return@invokeLater
            OpenFileDescriptor(project, location.file, location.line.coerceAtLeast(0), location.column.coerceAtLeast(0))
                .navigate(true)
        }
    }
}

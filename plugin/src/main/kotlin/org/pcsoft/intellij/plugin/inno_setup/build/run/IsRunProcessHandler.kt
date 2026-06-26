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

@file:Suppress("UnstableApiUsage")

package org.pcsoft.intellij.plugin.inno_setup.build.run

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.task.TaskRunnerResults
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputResolver
import org.pcsoft.intellij.plugin.inno_setup.build.IsCompilerService
import org.pcsoft.intellij.plugin.inno_setup.settings.IsBuildSettingsService
import java.io.File
import java.io.OutputStream
import java.util.*

/**
 * A [ProcessHandler] that drives the two-stage pipeline:
 * 1. Hand off to the build ([IsCompilerService]), which decides whether a recompile is needed and
 *    streams its output into the Build tool window.
 * 2. Launch the generated installer (or uninstaller).
 *
 * Stage 2 output is forwarded to the attached [com.intellij.execution.ui.ConsoleView].
 */
class IsRunProcessHandler(
    private val project: Project,
    private val config: IsRunConfiguration
) : ProcessHandler() {

    @Volatile private var activeHandler: OSProcessHandler? = null

    override fun startNotify() {
        super.startNotify()
        ApplicationManager.getApplication().executeOnPooledThread { runPipeline() }
    }

    override fun destroyProcessImpl() {
        activeHandler?.destroyProcess()
        notifyProcessTerminated(1)
    }

    override fun detachProcessImpl() = notifyProcessDetached()
    override fun detachIsDefault() = false
    override fun getProcessInput(): OutputStream? = null

    private fun runPipeline() {
        val compilerService = project.service<IsCompilerService>()
        if (compilerService.isccExecutable() == null) {
            printErr("Inno Setup is not configured. Set ISCC.exe path in Settings | Build | Inno Setup.\n")
            notifyProcessTerminated(1)
            return
        }

        // Determine ISCC output argument — always produce a real .exe even if project is DRY.
        val resolver = IsBuildOutputResolver(project)
        val projectMode = IsBuildOutputMode.fromName(
            IsBuildSettingsService.getInstance(project).state.outputMode
        )
        val scriptFile = File(config.scriptPath)
        val scriptOutputDir = IsBuildOutputResolver.parseOutputDir(
            runCatching { scriptFile.readText() }.getOrDefault("")
        )
        val buildRoot = resolver.buildRoot()

        // In project DRY mode no real setup.exe would be produced, so a temp output directory is
        // attached to (and reused by) this run configuration.
        if (projectMode == IsBuildOutputMode.DRY && config.persistentTempOutputDir.isBlank()) {
            config.persistentTempOutputDir =
                File(System.getProperty("java.io.tmpdir"), "inno-run-${UUID.randomUUID()}").path
        }
        val outputArg = IsScriptRunnerLogic.buildOutputArg(
            projectMode, buildRoot, scriptOutputDir, config.persistentTempOutputDir
        )
        val outputDir = IsScriptRunnerLogic.outputDirFromArg(outputArg) ?: File(buildRoot, "Output")

        // ── Stage 1: delegate to the build, which decides whether a recompile is necessary ─────
        // The build is reused when the participating files are unchanged and the installer still
        // exists; only then is compilation skipped. ISCC output appears in the Build tool window.
        val scriptVFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(scriptFile)
        if (scriptVFile == null) {
            printErr("Script file not found: ${config.scriptPath}\n")
            notifyProcessTerminated(1)
            return
        }
        val hasArtifact = IsScriptRunnerLogic.findSetupExe(outputDir) != null
        printOut("Building ${scriptFile.name} (see the Build tool window for details)…\n")
        if (compilerService.compileScriptForRun(scriptVFile, outputArg, hasArtifact) != TaskRunnerResults.SUCCESS) {
            printErr("Build failed — aborting run.\n")
            notifyProcessTerminated(1)
            return
        }

        // ── Stage 2: launch installer ─────────────────────────────────────────
        val exeFile: File? = IsScriptRunnerLogic.findSetupExe(outputDir)

        if (exeFile == null) {
            printErr("Setup executable not found in output directory: ${outputDir.path}\n")
            notifyProcessTerminated(1)
            return
        }

        val logFile = if (config.debugOutput) File.createTempFile("inno-run-", ".log") else null
        val launchArgs = IsScriptRunnerLogic.buildInstallerArgs(
            exeFile,
            config.languageOverride.takeIf { it.isNotBlank() },
            config.debugOutput, logFile
        )
        printOut("Launching ${exeFile.name}…\n")
        val launchCmd = GeneralCommandLine(launchArgs)
        config.envData.configureCommandLine(launchCmd, true)
        val success = runProcess(launchCmd)

        // Stream log file content after process finishes.
        logFile?.takeIf { it.exists() }?.forEachLine { printOut("  $it\n") }
        logFile?.delete()

        printOut(if (success) "Finished successfully.\n" else "Finished with errors.\n")
        notifyProcessTerminated(if (success) 0 else 1)
    }

    /** Runs [cmd], forwarding stdout/stderr to the console. Returns true on exit code 0. */
    private fun runProcess(cmd: GeneralCommandLine): Boolean {
        return try {
            val handler = OSProcessHandler(cmd)
            activeHandler = handler
            handler.addProcessListener(object : ProcessListener {
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    notifyTextAvailable(event.text, outputType)
                }
                override fun processTerminated(event: ProcessEvent) {}
            })
            handler.startNotify()
            handler.waitFor()
            activeHandler = null
            handler.exitCode == 0
        } catch (e: Exception) {
            printErr("Error: ${e.message}\n")
            false
        }
    }

    private fun printOut(text: String) = notifyTextAvailable(text, ProcessOutputTypes.STDOUT)
    private fun printErr(text: String) = notifyTextAvailable(text, ProcessOutputTypes.STDERR)
}

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

import com.intellij.build.BuildViewManager
import com.intellij.build.DefaultBuildDescriptor
import com.intellij.build.FilePosition
import com.intellij.build.events.BuildEvent
import com.intellij.build.events.MessageEvent
import com.intellij.build.events.impl.FailureResultImpl
import com.intellij.build.events.impl.FileMessageEventImpl
import com.intellij.build.events.impl.FinishBuildEventImpl
import com.intellij.build.events.impl.MessageEventImpl
import com.intellij.build.events.impl.OutputBuildEventImpl
import com.intellij.build.events.impl.StartBuildEventImpl
import com.intellij.build.events.impl.SuccessResultImpl
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
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
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.settings.IsBuildSettingsService
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsConfigurable
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsService
import java.io.File

/**
 * Compiles Inno Setup scripts with ISCC and streams the output into the IDE Build tool window as a
 * dedicated "Inno Setup" build session. Shared by the project-build hook ([compileProject]) and the
 * per-file context-menu action ([compileScript]).
 */
@Service(Service.Level.PROJECT)
class IsCompilerService(private val project: Project) {

    private val group = "Inno Setup"

    /**
     * Compiles all top-level scripts in the project. Blocking — intended to be called off the EDT
     * from the build pipeline. Returns the aggregate task-runner result.
     */
    fun compileProject(): ProjectTaskRunner.Result {
        val scripts = IsScriptCollector(project).collectTopLevel()
        return runCompilation(scripts, PluginBundle.message("build.title.project"))
    }

    /**
     * Compiles a single [scriptFile] on a pooled thread (used by the context-menu action).
     */
    fun compileScript(scriptFile: VirtualFile) {
        ApplicationManager.getApplication().executeOnPooledThread {
            runCompilation(listOf(scriptFile), PluginBundle.message("build.title.script", scriptFile.name))
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

    private fun runCompilation(scripts: List<VirtualFile>, title: String): ProjectTaskRunner.Result {
        // Flush unsaved editor changes so ISCC compiles the current content from disk.
        ApplicationManager.getApplication().invokeAndWait {
            FileDocumentManager.getInstance().saveAllDocuments()
        }

        val buildId = Any()
        val view = project.service<BuildViewManager>()
        val now = System.currentTimeMillis()
        val descriptor = DefaultBuildDescriptor(buildId, group, project.basePath ?: ".", now)
            .withExecutionFilter(IsBuildOutputFilter(project))
        view.onEvent(buildId, StartBuildEventImpl(descriptor, "$group: $title"))

        var hasErrors = false
        var firstError: Pair<VirtualFile, FilePosition>? = null

        val iscc = isccExecutable()
        if (iscc == null) {
            view.onEvent(
                buildId,
                MessageEventImpl(
                    buildId, MessageEvent.Kind.ERROR, group,
                    PluginBundle.message("build.not_configured.title"),
                    PluginBundle.message("build.not_configured.detail")
                )
            )
            view.onEvent(buildId, FinishBuildEventImpl(buildId, null, System.currentTimeMillis(), PluginBundle.message("build.finish.failed"), FailureResultImpl()))
            openSettings()
            return TaskRunnerResults.FAILURE
        }

        if (scripts.isEmpty()) {
            view.onEvent(buildId, OutputBuildEventImpl(buildId, PluginBundle.message("build.no_scripts") + "\n", true))
        }

        val resolver = IsBuildOutputResolver(project)
        val mode = outputMode()

        for (script in scripts) {
            view.onEvent(buildId, OutputBuildEventImpl(buildId, PluginBundle.message("build.compiling", script.name) + "\n", true))
            val outputArg = resolver.resolveOutputArg(script, mode)
            val commandLine = buildCommandLine(iscc, script.path, script.parent?.path, outputArg)

            // Groups consecutive output lines per section into a Build-tree message node.
            val grouper = IsBuildOutputGrouper { section -> emitSection(view, buildId, section) }

            try {
                val handler = OSProcessHandler(commandLine)
                handler.addProcessListener(object : ProcessListener {
                    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                        val stdout = outputType === ProcessOutputTypes.STDOUT
                        for (rawLine in event.text.split('\n')) {
                            if (rawLine.isBlank()) continue
                            when (val parsed = IsBuildOutputParser.parseLine(rawLine)) {
                                is IsBuildOutputParser.Line.Output -> {
                                    // Full log in the root console (indented lines folded by the
                                    // console-folding); per-section nodes are added on flush.
                                    view.onEvent(buildId, OutputBuildEventImpl(buildId, parsed.text + "\n", stdout))
                                    grouper.accept(parsed.text)
                                }

                                is IsBuildOutputParser.Line.Problem -> {
                                    // Keep diagnostics in order relative to grouped output and emit
                                    // them live (navigable) at the build root.
                                    grouper.flush()
                                    if (!parsed.warning) hasErrors = true
                                    val vFile = parsed.file?.let { resolvePath(it) }
                                    problemEvents(buildId, group, parsed, vFile?.let { File(it.path) })
                                        .forEach { view.onEvent(buildId, it) }
                                    if (!parsed.warning && firstError == null && vFile != null && parsed.line != null) {
                                        firstError = vFile to FilePosition(File(vFile.path), parsed.line, parsed.column ?: 0)
                                    }
                                }
                            }
                        }
                    }
                })
                handler.startNotify()
                handler.waitFor()
                grouper.flush()
                if (handler.exitCode != 0) hasErrors = true
            } catch (e: Exception) {
                thisLogger().warn("Failed to run ISCC for ${script.path}", e)
                hasErrors = true
                view.onEvent(
                    buildId,
                    MessageEventImpl(buildId, MessageEvent.Kind.ERROR, group, PluginBundle.message("build.run_failed", e.message ?: ""), null)
                )
            }
        }

        firstError?.let { (vFile, pos) -> navigateTo(vFile, pos) }

        val result = if (hasErrors) FailureResultImpl() else SuccessResultImpl()
        view.onEvent(
            buildId,
            FinishBuildEventImpl(
                buildId, null, System.currentTimeMillis(),
                if (hasErrors) PluginBundle.message("build.finish.failed") else PluginBundle.message("build.finish.success"),
                result
            )
        )
        return if (hasErrors) TaskRunnerResults.FAILURE else TaskRunnerResults.SUCCESS
    }

    /**
     * Emits a completed [section][IsBuildOutputGrouper.OutputSection] as a Build-tree node. A
     * [MessageEvent] is always shown regardless of the overall build status; its severity reflects
     * whether the section's lines mention an error or warning (otherwise [MessageEvent.Kind.INFO]).
     * The section's raw lines become the node's detail.
     */
    private fun emitSection(view: BuildViewManager, buildId: Any, section: IsBuildOutputGrouper.OutputSection) {
        view.onEvent(
            buildId,
            MessageEventImpl(buildId, severityOf(section.lines), group, section.title, section.lines.joinToString("\n"))
        )
    }

    /** Picks the node severity from the section's content: ERROR wins over WARNING over INFO. */
    private fun severityOf(lines: List<String>): MessageEvent.Kind = when {
        lines.any { it.contains("error", ignoreCase = true) } -> MessageEvent.Kind.ERROR
        lines.any { it.contains("warning", ignoreCase = true) } -> MessageEvent.Kind.WARNING
        else -> MessageEvent.Kind.INFO
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

    private fun resolvePath(path: String): VirtualFile? =
        LocalFileSystem.getInstance().findFileByPath(path.replace('\\', '/'))

    private fun navigateTo(file: VirtualFile, pos: FilePosition) {
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed || !file.isValid) return@invokeLater
            OpenFileDescriptor(project, file, pos.startLine.coerceAtLeast(0), pos.startColumn.coerceAtLeast(0))
                .navigate(true)
        }
    }

    companion object {
        /**
         * Builds the Build-window events for a single ISCC problem line. Pure (no project/VFS), so it
         * is unit-testable. Returns, in order:
         *  1. an [OutputBuildEventImpl] carrying the full raw line — keeps the text in the root
         *     overview console (stderr for errors, stdout for warnings);
         *  2. a [FileMessageEventImpl] (navigable, with [FilePosition]) when [resolvedFile] and the
         *     line number are known, otherwise a positionless [MessageEventImpl].
         * Both message events use the short error text as title and the full raw line as detail.
         */
        fun problemEvents(
            buildId: Any,
            group: String,
            parsed: IsBuildOutputParser.Line.Problem,
            resolvedFile: File?
        ): List<BuildEvent> {
            val kind = if (parsed.warning) MessageEvent.Kind.WARNING else MessageEvent.Kind.ERROR
            val output = OutputBuildEventImpl(buildId, parsed.detail + "\n", parsed.warning)
            val message: BuildEvent = if (resolvedFile != null && parsed.line != null) {
                FileMessageEventImpl(
                    buildId, kind, group, parsed.message, parsed.detail,
                    FilePosition(resolvedFile, parsed.line, parsed.column ?: 0)
                )
            } else {
                MessageEventImpl(buildId, kind, group, parsed.message, parsed.detail)
            }
            return listOf(output, message)
        }

        /**
         * Builds the ISCC command line. Pure with respect to IntelliJ project state so it can be
         * unit-tested: the script path, optional working directory and optional `/O…` argument are
         * assembled into a [GeneralCommandLine].
         */
        fun buildCommandLine(
            iscc: File,
            scriptPath: String,
            workDir: String?,
            outputArg: String?
        ): GeneralCommandLine {
            val cmd = GeneralCommandLine(iscc.absolutePath)
            outputArg?.let { cmd.addParameter(it) }
            cmd.addParameter(scriptPath)
            if (workDir != null) cmd.withWorkDirectory(workDir)
            return cmd
        }
    }
}

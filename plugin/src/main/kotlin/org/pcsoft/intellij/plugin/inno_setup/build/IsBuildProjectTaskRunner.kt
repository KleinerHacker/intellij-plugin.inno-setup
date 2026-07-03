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

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.task.ModuleBuildTask
import com.intellij.task.ProjectTask
import com.intellij.task.ProjectTaskContext
import com.intellij.task.ProjectTaskRunner
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import org.pcsoft.intellij.plugin.inno_setup.settings.IsBuildSettingsService

/**
 * Hooks Inno Setup compilation into the IDE's *Build Project* pipeline. Participates in module build
 * tasks when compilation on build is enabled (per project) and a valid ISCC installation is
 * configured (IDE-wide), delegating the actual work to [IsCompilerService].
 */
class IsBuildProjectTaskRunner : ProjectTaskRunner() {

    // Obsolete project-less overload: still abstract on the 253 baseline we compile against, so the
    // override is mandatory. Opt out here and rely on the project-aware canRun below (which the
    // platform actually calls). Scheduled for removal in newer platforms.
    @Suppress("OVERRIDE_DEPRECATION")
    override fun canRun(projectTask: ProjectTask): Boolean = false

    /**
     * Participates only in module build tasks, and only when compilation on build is enabled for the
     * project and a valid ISCC installation is configured — otherwise the runner stays out entirely.
     */
    override fun canRun(project: Project, projectTask: ProjectTask, context: ProjectTaskContext?): Boolean {
        if (projectTask !is ModuleBuildTask) return false
        if (!IsBuildSettingsService.getInstance(project).state.compileOnBuild) return false
        return !IsSettingsService.getInstance().state.installationPath.isNullOrBlank()
    }

    /**
     * Compiles the project's top-level scripts on a pooled thread and resolves the promise with the
     * aggregate result, so a compiler failure fails the overall build.
     */
    override fun run(
        project: Project,
        context: ProjectTaskContext,
        vararg tasks: ProjectTask
    ): Promise<Result> {
        // Rebuild (vs. incremental Build) is signalled by a non-incremental module build task; it
        // forces a full recompile, bypassing the hash-based up-to-date check.
        val force = tasks.filterIsInstance<ModuleBuildTask>().any { !it.isIncrementalBuild }
        val promise = AsyncPromise<Result>()
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                promise.setResult(project.service<IsCompilerService>().compileProject(force))
            } catch (e: Throwable) {
                promise.setError(e)
            }
        }
        return promise
    }
}

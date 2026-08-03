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

package org.pcsoft.intellij.plugin.inno_setup.build.config

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.annotations.TestOnly
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSymbolTracker
import java.io.File

/**
 * Stores the project's [IsBuildConfiguration]s as one XML file each in a `.build` directory next to
 * `.idea`, mirroring how the platform keeps shareable run configurations in `.run`.
 *
 * Plain files rather than a `PersistentStateComponent` on purpose: one file per configuration is what makes
 * them reviewable and mergeable in version control, and lets a team add a configuration without touching a
 * shared settings blob.
 *
 * The directory is the single source of truth; results are cached only until the files on disk change, so an
 * externally edited or pulled configuration is picked up without an IDE restart.
 */
@Service(Service.Level.PROJECT)
class IsBuildConfigurationService(private val project: Project) {

    private val lock = Any()

    private var cached: List<IsBuildConfiguration>? = null
    private var cachedStamp: String? = null

    /**
     * Replaces the resolved [directory] — the seam that makes the store testable, since the light test
     * project has no base directory on disk to put a `.build` next to.
     */
    @set:TestOnly
    var directoryOverride: File? = null
        set(value) {
            field = value
            synchronized(lock) { invalidate() }
        }

    /**
     * The `.build` directory of this project, whether or not it exists yet, or `null` for a project without
     * a base directory on disk. The parent must exist: a base path pointing nowhere real (an in-memory test
     * project, a project directory that has been deleted) must not make the plugin create a stray `.build`
     * somewhere up the tree.
     */
    val directory: File?
        get() = directoryOverride
            ?: project.basePath
                ?.let { File(it) }
                ?.takeIf { it.isDirectory }
                ?.let { File(it, DIRECTORY_NAME) }

    /**
     * All configurations, sorted by name. Never empty for a project that has been initialised — see
     * [ensureDefaults], which is what a freshly set up project goes through.
     */
    fun all(): List<IsBuildConfiguration> = synchronized(lock) {
        val stamp = diskStamp()
        cached?.takeIf { cachedStamp == stamp }?.let { return it }
        val loaded = load()
        cached = loaded
        cachedStamp = stamp
        loaded
    }

    /** The configuration called [name], or `null` when no such configuration exists (any more). */
    fun byName(name: String?): IsBuildConfiguration? =
        name?.takeIf { it.isNotBlank() }?.let { wanted -> all().firstOrNull { it.name == wanted } }

    /**
     * The configuration a run should fall back to when it names none: the `Debug` default if present,
     * otherwise the first one. `null` only for a project without any configuration at all.
     */
    fun defaultConfiguration(): IsBuildConfiguration? {
        val configs = all()
        return configs.firstOrNull { it.name == IsBuildConfiguration.DEFAULT_DEBUG_NAME } ?: configs.firstOrNull()
    }

    /** Creates or overwrites the file of [config]. */
    fun save(config: IsBuildConfiguration) {
        val dir = directory ?: return
        synchronized(lock) {
            if (!dir.isDirectory && !dir.mkdirs()) {
                thisLogger().warn("Cannot create build configuration directory ${dir.path}")
                return
            }
            runCatching { File(dir, IsBuildConfigurationXml.fileNameFor(config.name)).writeText(IsBuildConfigurationXml.toText(config)) }
                .onFailure { thisLogger().warn("Cannot write build configuration '${config.name}'", it) }
            invalidate()
        }
        refreshVfs()
    }

    /** Removes the configuration called [name]; a name that does not exist is silently ignored. */
    fun delete(name: String) {
        val dir = directory ?: return
        synchronized(lock) {
            File(dir, IsBuildConfigurationXml.fileNameFor(name)).delete()
            invalidate()
        }
        refreshVfs()
    }

    /**
     * Makes the directory contain exactly [configs] — the commit path of the settings page, where the user
     * may have added, edited, renamed and removed configurations in one go. Files of configurations that are
     * no longer present are deleted.
     */
    fun replaceAll(configs: List<IsBuildConfiguration>) {
        val dir = directory ?: return
        synchronized(lock) {
            val keep = configs.map { IsBuildConfigurationXml.fileNameFor(it.name) }.toSet()
            dir.listFiles { f -> f.isFile && f.name.endsWith(IsBuildConfigurationXml.FILE_SUFFIX) }
                ?.filter { it.name !in keep }
                ?.forEach { it.delete() }
            invalidate()
        }
        configs.forEach { save(it) }
    }

    /**
     * Creates the two starter configurations ([IsBuildConfiguration.defaults]) when the project has none.
     * Idempotent, so it can run on every project open.
     */
    fun ensureDefaults() {
        if (all().isNotEmpty()) return
        IsBuildConfiguration.defaults().forEach { save(it) }
    }

    /**
     * Drops the cached configurations. Also bumps [IsPreprocessorSymbolTracker], because a configuration that
     * has been written, deleted or replaced changes the `/D…` symbols the branch analysis was cached with —
     * the settings page applies its changes through here, without ever touching an open document.
     */
    private fun invalidate() {
        cached = null
        cachedStamp = null
        if (!project.isDisposed) {
            IsPreprocessorSymbolTracker.getInstance(project).symbolsChanged()
        }
    }

    private fun files(): List<File> =
        directory?.listFiles { f -> f.isFile && f.name.endsWith(IsBuildConfigurationXml.FILE_SUFFIX) }
            ?.sortedBy { it.name }
            ?: emptyList()

    /** Cheap fingerprint of the directory content, so the cache survives reads but not external edits. */
    private fun diskStamp(): String =
        files().joinToString("|") { it.name + ':' + it.lastModified() + ':' + it.length() }

    private fun load(): List<IsBuildConfiguration> =
        files()
            .mapNotNull { file ->
                runCatching { IsBuildConfigurationXml.fromText(file.readText()) }
                    .onFailure { thisLogger().warn("Cannot read build configuration ${file.path}", it) }
                    .getOrNull()
            }
            .distinctBy { it.name }
            .sortedBy { it.name }

    /** Makes the IDE notice the files written behind its back, so VCS and the project view stay in sync. */
    private fun refreshVfs() {
        val dir = directory ?: return
        LocalFileSystem.getInstance().refreshAndFindFileByIoFile(dir)?.refresh(true, true)
    }

    companion object {

        /** Directory name next to `.idea` holding one file per build configuration. */
        const val DIRECTORY_NAME: String = ".build"

        /** Returns the build configuration service of [project]. */
        fun getInstance(project: Project): IsBuildConfigurationService =
            project.getService(IsBuildConfigurationService::class.java)
    }
}

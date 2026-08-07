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

package org.pcsoft.intellij.plugin.inno_setup.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.IOException
import java.net.URI
import javax.inject.Inject

/**
 * Fetches the official Inno Setup example scripts (`Examples/` of `jrsoftware/issrc`) into a build directory
 * so the integration tests can validate the plugin against them.
 *
 * The examples are third-party sources and MUST NOT be checked into this repository: they are downloaded from
 * a pinned upstream tag, used by the tests and removed again afterwards (see the companion clean task wired in
 * `language/script/build.gradle.kts`). Only the top-level files of `Examples/` are extracted — that is every
 * `*.iss` plus the payload files they reference (`MyProg.exe`, `License.txt`, …), which the file references
 * and the `GetVersionNumbersString` computations of the scripts need to resolve.
 */
abstract class InnoSetupExamplesTask : DefaultTask() {

    /** The upstream git tag to fetch, e.g. `is-6_7_3`. Pinned so upstream changes cannot break the build. */
    @get:Input
    abstract val tag: Property<String>

    /** Directory the top-level `Examples/` files are extracted into. */
    @get:OutputDirectory
    abstract val examplesDir: DirectoryProperty

    /** Scratch location of the downloaded source archive; removed together with [examplesDir]. */
    @get:Internal
    abstract val archive: RegularFileProperty

    @get:Inject
    protected abstract val archives: ArchiveOperations

    @get:Inject
    protected abstract val fs: FileSystemOperations

    /** Downloads the pinned upstream archive and extracts the top-level example files. */
    @TaskAction
    fun fetch() {
        val url = "$BASE_URL/${tag.get()}"
        val archiveFile = archive.get().asFile
        archiveFile.parentFile.mkdirs()

        try {
            URI(url).toURL().openStream().use { input ->
                archiveFile.outputStream().use { output -> input.copyTo(output) }
            }
        } catch (e: IOException) {
            throw GradleException(
                "Could not download the Inno Setup examples from $url. The integration tests validate the " +
                        "plugin against the official example scripts, which are fetched on demand and are not " +
                        "part of this repository — network access is required. Run `./gradlew developerTest` " +
                        "for the offline suite.",
                e,
            )
        }

        val target = examplesDir.get().asFile
        fs.delete { delete(target) }
        fs.copy {
            // `*/Examples/*` deliberately does not cross directory boundaries: only the top-level example
            // files are taken, not the MyProg/MyDll source trees below them.
            from(archives.tarTree(archives.gzip(archiveFile))) {
                include("*/Examples/*")
                // Strip the `issrc-<tag>/Examples/` prefix so the tests see a flat corpus directory.
                eachFile { relativePath = RelativePath(true, relativePath.lastName) }
            }
            into(target)
            includeEmptyDirs = false
        }

        val scripts = target.listFiles().orEmpty().count { it.isFile && it.extension.equals("iss", true) }
        if (scripts == 0) {
            throw GradleException(
                "The archive of tag '${tag.get()}' contains no *.iss files under Examples/ — the upstream " +
                        "layout changed. Adjust the tag or the extraction pattern in InnoSetupExamplesTask."
            )
        }
        logger.lifecycle("Fetched $scripts Inno Setup example scripts (tag ${tag.get()}) into $target")
    }

    private companion object {
        const val BASE_URL = "https://codeload.github.com/jrsoftware/issrc/tar.gz/refs/tags"
    }
}

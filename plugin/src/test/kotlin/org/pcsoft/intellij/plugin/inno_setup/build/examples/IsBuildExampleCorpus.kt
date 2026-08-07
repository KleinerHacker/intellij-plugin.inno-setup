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

package org.pcsoft.intellij.plugin.inno_setup.build.examples

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

/**
 * Access to the official Inno Setup example corpus and to the local Inno Setup installation used by
 * [IsExampleScriptBuildIT].
 *
 * The corpus is **not** part of this repository: `:plugin:downloadInnoSetupExamples` fetches it from a pinned
 * upstream tag into the build directory, hands its location over through the `innoSetup.examples.dir` system
 * property and deletes it again after the test run. The Inno Setup installation the scripts are compiled with
 * comes from `innoSetup.installation.dir` (see `-PinnoSetupHome` / `INNO_SETUP_HOME`).
 *
 * Unlike the analysis-only corpus of `:language:script`, both are hard requirements here — a build test
 * without a compiler is not a build test, so a missing corpus or a missing `ISCC.exe` fails the suite.
 */
object IsBuildExampleCorpus {

    /** The corpus directory, or `null` when it was not provided for this run. */
    val directory: File? =
        System.getProperty("innoSetup.examples.dir")
            ?.let(::File)
            ?.takeIf { it.isDirectory && it.listFiles().orEmpty().any { f -> f.extension.equals("iss", true) } }

    /** Every `*.iss` of the corpus, sorted by name so failures are reproducible. */
    val scripts: List<File>
        get() = directory?.listFiles().orEmpty()
            .filter { it.isFile && it.extension.equals("iss", true) }
            .sortedBy { it.name }

    /** The configured Inno Setup installation directory, or `null` when it does not exist. */
    val installationDir: File? =
        System.getProperty("innoSetup.installation.dir")
            ?.takeIf { it.isNotBlank() }
            ?.let(::File)
            ?.takeIf { it.isDirectory }

    /** The `ISCC.exe` of [installationDir], or `null` when there is no usable installation. */
    val isccExecutable: File?
        get() = installationDir?.resolve("ISCC.exe")?.takeIf { it.isFile }

    /**
     * Examples that cannot be compiled from the corpus alone, mapped script name → reason.
     *
     * `InnoSetupExamplesTask` extracts only the top-level files of `Examples/`, so an example referencing a
     * build product of the `MyDll`/`MyProg` source trees below it has no payload to package. Such a script is
     * excluded here with an explicit reason rather than silently tolerated.
     */
    val skipped: Map<String, String> by lazy {
        val file = File(SKIP_RESOURCE)
        check(file.isFile) { "Missing build skip list: $file" }
        ObjectMapper(YAMLFactory())
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(file, SkipList::class.java)
            .examples
    }

    /** Payload of `build-skip.yaml`. */
    private data class SkipList(val examples: Map<String, String> = emptyMap())

    private const val SKIP_RESOURCE = "src/test/resources/examples/build-skip.yaml"
}

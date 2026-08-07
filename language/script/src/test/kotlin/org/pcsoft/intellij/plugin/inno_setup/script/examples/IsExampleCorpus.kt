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

package org.pcsoft.intellij.plugin.inno_setup.script.examples

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

/**
 * Access to the official Inno Setup example corpus used by `IsExampleScriptsIT`.
 *
 * The corpus is **not** part of this repository: `:language:script:downloadInnoSetupExamples` fetches it from
 * a pinned upstream tag into the build directory, hands its location over through the `innoSetup.examples.dir`
 * system property and deletes it again after the test run. When the property is absent (a bare IDE run without
 * the Gradle wiring) [directory] is `null` and the tests skip themselves.
 */
object IsExampleCorpus {

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

    /** Whether missing/outdated fingerprint files are to be written instead of failing the test. */
    val updateFingerprints: Boolean = System.getProperty("innoSetup.examples.updateFingerprints") == "true"

    /** Directory of the committed matrix and fingerprint resources. */
    val expectationDir: File = File("src/test/resources/examples")

    /** One set of externally supplied preprocessor symbols an example is validated with. */
    data class Combination(
        /** Stable identity used as the `*.expected` file name part; `DEFAULT` when nothing is defined. */
        val key: String,
        /** Symbols handed to the analysis, mapped name → value. */
        val defines: Map<String, String?>,
    )

    /** The fingerprint resource holding the expected effective script of [script] under [combination]. */
    fun expectationFile(script: File, combination: Combination): File =
        File(expectationDir, "${script.name}@${combination.key}.expected")

    /**
     * Every combination of externally supplied preprocessor symbols [script] has to be validated with.
     *
     * The candidate symbols are the names referenced by `#ifdef` / `#ifndef` / `#if` that the script does not
     * define itself, minus the ISPP built-ins listed in `matrix.yaml`; the result is their full cross product
     * of "not defined" and "defined". `matrix.yaml` may override the value set, ignore further names or raise
     * the combination limit per example.
     *
     * @throws IllegalStateException when the cross product exceeds the configured limit — that is a signal to
     *   pin the example down explicitly rather than to let the suite explode.
     */
    fun combinationsOf(script: File): List<Combination> {
        val text = script.readText()
        val override = matrix.examples[script.name]
        val ignored = (matrix.ignoredSymbols + override?.ignoredSymbols.orEmpty()).map { it.lowercase() }.toSet()
        val defined = DEFINE.findAll(text).map { it.groupValues[2].lowercase() }.toSet()

        val candidates = linkedSetOf<String>()
        IFDEF.findAll(text).forEach { candidates += it.groupValues[2] }
        IF.findAll(text).forEach { match ->
            IDENTIFIER.findAll(match.groupValues[2]).forEach { candidates += it.value }
        }
        override?.values?.keys?.forEach { candidates += it }

        val symbols = candidates
            .filter { it.lowercase() !in ignored && it.lowercase() !in defined }
            .sorted()

        val limit = override?.maxCombinations ?: matrix.maxCombinations
        val valuesOf = { name: String -> override?.values?.get(name) ?: DEFAULT_VALUES }
        val total = symbols.fold(1) { acc, name -> acc * valuesOf(name).size }
        check(total <= limit) {
            "${script.name} yields $total preprocessor combinations (symbols: ${symbols.joinToString()}), " +
                    "which exceeds the limit of $limit. Add an explicit entry for it to " +
                    "${File(expectationDir, "matrix.yaml")}."
        }

        var result = listOf(emptyMap<String, String?>())
        symbols.forEach { name ->
            result = result.flatMap { base ->
                valuesOf(name).map { value -> if (value.isEmpty()) base else base + (name to value) }
            }
        }
        return result.map { defines ->
            val key = defines.entries.sortedBy { it.key }
                .joinToString("_") { "${it.key}=${it.value}" }
                .ifEmpty { "DEFAULT" }
            Combination(key, defines)
        }
    }

    // ── matrix.yaml ──────────────────────────────────────────────────────────────────────────

    private data class ExampleOverride(
        val values: Map<String, List<String>> = emptyMap(),
        val ignoredSymbols: List<String> = emptyList(),
        val maxCombinations: Int? = null,
    )

    private data class Matrix(
        val ignoredSymbols: List<String> = emptyList(),
        val maxCombinations: Int = 16,
        val examples: Map<String, ExampleOverride> = emptyMap(),
    )

    private val matrix: Matrix by lazy {
        val file = File(expectationDir, "matrix.yaml")
        check(file.isFile) { "Missing combination matrix: $file" }
        ObjectMapper(YAMLFactory())
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(file, Matrix::class.java)
    }

    /** An empty value means "symbol not defined at all"; `1` is the neutral "defined" value ISCC's `/D` uses. */
    private val DEFAULT_VALUES = listOf("", "1")

    private val IFDEF = Regex("""(?im)^[ \t]*#[ \t]*(ifdef|ifndef)[ \t]+([A-Za-z_]\w*)""")
    // Group 2 holds the expression, matching the group numbering of [IFDEF].
    private val IF = Regex("""(?im)^[ \t]*#[ \t]*(if|elif)[ \t]+(.*)$""")
    private val DEFINE = Regex("""(?im)^[ \t]*#[ \t]*(define|dim|sub|macro)[ \t]+(?:public[ \t]+)?([A-Za-z_]\w*)""")
    private val IDENTIFIER = Regex("""[A-Za-z_]\w*""")
}

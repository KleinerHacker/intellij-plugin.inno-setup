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

package org.pcsoft.intellij.plugin.inno_setup.build.run

/**
 * Parses the compiler-defines field of a run configuration into ISCC `/D` symbols.
 *
 * The same parsed result feeds two consumers that must not disagree: the compiler invocation, which turns it
 * into `/D…` command-line arguments, and the editor's conditional-branch analysis, which seeds it as
 * externally defined symbols. If they diverged, the IDE would grey out branches the build actually compiles.
 */
object IsCompilerDefines {

    /** Separators accepted between entries: any whitespace, semicolons and commas. */
    private val SEPARATORS = Regex("[\\s;,]+")

    /** Whitespace around an `=`, removed first so `A = 1` is one entry and not three. */
    private val SPACED_ASSIGNMENT = Regex("\\s*=\\s*")

    /**
     * Parses [text] into an ordered name → value map. Accepted per entry: `NAME`, `NAME=value` and the
     * literal ISCC form `/DNAME=value`. A `null` value means the symbol is defined but carries no value,
     * which is what makes `#ifdef NAME` decidable while `#if NAME > 2` stays undecidable.
     *
     * Entries are separated by whitespace, commas or semicolons; spaces directly around an `=` bind to the
     * assignment instead. A value therefore cannot itself contain a space — the same restriction a `/D`
     * argument has on the command line without quoting.
     *
     * Later entries win over earlier ones with the same name, matching ISCC's own last-one-wins behaviour.
     */
    fun parse(text: String): Map<String, String?> {
        val result = LinkedHashMap<String, String?>()
        text.replace(SPACED_ASSIGNMENT, "=")
            .split(SEPARATORS)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { entry ->
                val body = entry.removePrefix("/D").removePrefix("/d").ifEmpty { return@forEach }
                val separator = body.indexOf('=')
                if (separator < 0) {
                    result[body] = null
                } else {
                    val name = body.substring(0, separator).trim()
                    if (name.isNotEmpty()) result[name] = body.substring(separator + 1).trim()
                }
            }
        return result
    }

    /** ISCC command-line arguments for [text], one `/D` argument per defined symbol. */
    fun toIsccArguments(text: String): List<String> =
        parse(text).map { (name, value) -> if (value == null) "/D$name" else "/D$name=$value" }
}

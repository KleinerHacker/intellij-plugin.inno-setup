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

import org.pcsoft.intellij.plugin.inno_setup.build.run.IsCompilerDefines
import java.security.MessageDigest

/**
 * A named set of compile options, the Inno Setup equivalent of a C/C++ build configuration.
 *
 * A run configuration only *references* a build configuration by [name]; the content lives here so several
 * runs can share one option set and a change to it is picked up everywhere at once.
 *
 * @property name                  unique, user-visible identifier; also the file name under `.build/`
 * @property compilerSymbols       ISCC `/D…` preprocessor symbols in the syntax of [IsCompilerDefines]
 * @property outputDirOverride     replaces the resolved output directory (`/O…`); blank keeps the project rule
 * @property additionalIsccOptions further raw ISCC command-line options, whitespace-separated, quotable
 */
data class IsBuildConfiguration(
    val name: String,
    val compilerSymbols: String = "",
    val outputDirOverride: String = "",
    val additionalIsccOptions: String = ""
) {

    /** The `/D…` arguments this configuration contributes to the ISCC command line. */
    val isccDefines: List<String>
        get() = IsCompilerDefines.toIsccArguments(compilerSymbols)

    /** The extra raw ISCC options, split into single command-line arguments. */
    val isccOptions: List<String>
        get() = tokenize(additionalIsccOptions)

    /** The preprocessor symbols this configuration defines, for seeding the editor's `#ifdef` analysis. */
    val symbols: Map<String, String?>
        get() = IsCompilerDefines.parse(compilerSymbols)

    /**
     * Fingerprint of everything that influences the produced installer — deliberately **without** [name],
     * because renaming a configuration does not change what it compiles.
     *
     * Feeds the build's up-to-date check, so switching a run from `Debug` to `Release` (or merely editing
     * `Debug`'s symbols) invalidates the previously produced installer instead of reusing it.
     *
     * The symbols are normalised through [IsCompilerDefines.parse] first, so purely cosmetic edits
     * (`A,B` vs. `A B`, or a duplicate that ISCC would drop anyway) do not force a pointless rebuild.
     */
    fun contentHash(): String {
        val normalizedSymbols = symbols.entries.joinToString(" ") { (n, v) -> if (v == null) n else "$n=$v" }
        val payload = normalizedSymbols + '\u0000' +
                outputDirOverride.trim() + '\u0000' +
                isccOptions.joinToString(" ")
        val digest = MessageDigest.getInstance("MD5").digest(payload.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    companion object {

        /** Name of the default configuration created for a fresh project that defines no symbols. */
        const val DEFAULT_RELEASE_NAME: String = "Release"

        /** Name of the default configuration created for a fresh project that defines `DEBUG`. */
        const val DEFAULT_DEBUG_NAME: String = "Debug"

        /** The two configurations a project starts with: a plain one and a `DEBUG` one. */
        fun defaults(): List<IsBuildConfiguration> = listOf(
            IsBuildConfiguration(DEFAULT_RELEASE_NAME),
            IsBuildConfiguration(DEFAULT_DEBUG_NAME, compilerSymbols = "DEBUG")
        )

        /**
         * Splits a raw option string into arguments on whitespace, keeping `"…"` quoted runs together so
         * paths with spaces survive as one argument. The quotes themselves are retained, because ISCC
         * expects them in place (`/O"C:\My Out"`).
         */
        fun tokenize(text: String): List<String> {
            val result = mutableListOf<String>()
            val current = StringBuilder()
            var inQuotes = false
            for (ch in text) {
                when {
                    ch == '"' -> {
                        inQuotes = !inQuotes
                        current.append(ch)
                    }

                    ch.isWhitespace() && !inQuotes -> {
                        if (current.isNotEmpty()) {
                            result += current.toString()
                            current.setLength(0)
                        }
                    }

                    else -> current.append(ch)
                }
            }
            if (current.isNotEmpty()) result += current.toString()
            return result
        }
    }
}

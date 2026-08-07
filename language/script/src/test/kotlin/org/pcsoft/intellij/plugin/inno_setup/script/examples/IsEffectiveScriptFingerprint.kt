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

import java.security.MessageDigest

/**
 * The expected value of an effective script, reduced to a form that carries no third-party source: the
 * SHA-256 of the normalized effective text plus its structural summary (sections with entry counts and the
 * total line count).
 *
 * The example scripts of `jrsoftware/issrc` must not be checked into this repository, so a full golden file
 * is not an option. The hash pins the effective text down to the character, while the structural summary
 * makes a mismatch diagnosable without having the expected text at hand.
 */
data class IsEffectiveScriptFingerprint(
    /** SHA-256 (lower-case hex) of the normalized effective text. */
    val sha256: String,
    /** Number of lines of the normalized effective text. */
    val lines: Int,
    /** Section names in order of first occurrence, each with its number of entry lines. */
    val sections: List<Pair<String, Int>>,
) {

    /** The on-disk representation of this fingerprint, as stored in the `*.expected` resources. */
    fun render(): String = buildString {
        append("sha256: ").append(sha256).append('\n')
        append("lines: ").append(lines).append('\n')
        append("sections:\n")
        sections.forEach { (name, count) -> append("  ").append(name).append('=').append(count).append('\n') }
    }

    /** A human-readable difference against [other], listing only what actually differs. */
    fun diffTo(other: IsEffectiveScriptFingerprint): String = buildString {
        if (sha256 != other.sha256) append("  sha256: expected $sha256, but was ${other.sha256}\n")
        if (lines != other.lines) append("  lines: expected $lines, but was ${other.lines}\n")
        if (sections != other.sections) {
            append("  sections: expected ${sections.joinToString { "${it.first}=${it.second}" }}\n")
            append("            but was ${other.sections.joinToString { "${it.first}=${it.second}" }}\n")
        }
    }

    companion object {

        /** Matches a section header line such as `[Setup]`, capturing the header including its brackets. */
        private val SECTION_HEADER = Regex("""^\s*(\[[^]\r\n]+])\s*$""")

        /**
         * Normalizes [text] so the fingerprint only reacts to real content changes: line separators become
         * `\n`, trailing whitespace per line is dropped, and trailing blank lines collapse into a single
         * final newline.
         */
        fun normalize(text: String): String {
            val body = text.replace("\r\n", "\n").replace('\r', '\n')
                .lineSequence().joinToString("\n") { it.trimEnd() }
                .trimEnd('\n')
            return if (body.isEmpty()) "" else body + "\n"
        }

        /** Computes the fingerprint of the effective script [text] (normalized first). */
        fun of(text: String): IsEffectiveScriptFingerprint {
            val normalized = normalize(text)
            val digest = MessageDigest.getInstance("SHA-256").digest(normalized.toByteArray(Charsets.UTF_8))
            val lines = if (normalized.isEmpty()) 0 else normalized.trimEnd('\n').count { it == '\n' } + 1

            val sections = mutableListOf<Pair<String, Int>>()
            var current: String? = null
            var count = 0
            fun flush() = current?.let { sections += it to count }
            normalized.lineSequence().forEach { line ->
                val header = SECTION_HEADER.matchEntire(line)
                if (header != null) {
                    flush()
                    current = header.groupValues[1]
                    count = 0
                } else if (current != null && line.isNotBlank() && !line.trimStart().startsWith(";")) {
                    count++
                }
            }
            flush()

            return IsEffectiveScriptFingerprint(digest.joinToString("") { "%02x".format(it) }, lines, sections)
        }

        /** Reads back a fingerprint produced by [render]. */
        fun parse(text: String): IsEffectiveScriptFingerprint {
            var sha = ""
            var lines = -1
            val sections = mutableListOf<Pair<String, Int>>()
            var inSections = false
            text.lineSequence().forEach { raw ->
                val line = raw.trimEnd()
                when {
                    line.startsWith("sha256:") -> sha = line.substringAfter(':').trim()
                    line.startsWith("lines:") -> lines = line.substringAfter(':').trim().toInt()
                    line.trim() == "sections:" -> inSections = true
                    inSections && line.isNotBlank() -> {
                        val name = line.substringBeforeLast('=').trim()
                        sections += name to line.substringAfterLast('=').trim().toInt()
                    }
                }
            }
            require(sha.isNotEmpty() && lines >= 0) { "Malformed fingerprint record:\n$text" }
            return IsEffectiveScriptFingerprint(sha, lines, sections)
        }
    }
}

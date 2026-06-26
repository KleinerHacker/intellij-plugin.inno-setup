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

/**
 * Pure parser for ISCC console output. Turns raw compiler lines into structured results so the
 * compiler service can render plain output, navigable error links, and positionless problems alike.
 *
 * It is deliberately free of any IntelliJ dependency so it can be unit-tested in isolation.
 */
object IsBuildOutputParser {

    /** A single parsed ISCC output line. */
    sealed interface Line {
        /** An ordinary, non-diagnostic console line that is shown verbatim. */
        data class Output(val text: String) : Line

        /**
         * A compiler problem. [file], [line] and [column] are present only when ISCC reported a
         * position; [line]/[column] are **0-based** (already converted from ISCC's 1-based values)
         * so they can be handed to `FilePosition` directly. [message] is the short error text (tree
         * node title); [detail] is the full original ISCC line (console / detail panel).
         */
        data class Problem(
            val message: String,
            val detail: String,
            val file: String?,
            val line: Int?,
            val column: Int?,
            val warning: Boolean
        ) : Line
    }

    // "Error on line 23 in C:\path\script.iss: message"
    // "Error on line 23, column 5 in C:\path\script.iss: message"
    // The file is non-greedy and the separator requires whitespace after the colon so a Windows
    // drive-letter colon (e.g. "C:\") is not mistaken for the "file: message" separator.
    private val POSITIONAL = Regex(
        """^(error|warning) on line (\d+)(?:,?\s*column (\d+))? in (.+?):\s+(.*)$""",
        RegexOption.IGNORE_CASE
    )

    // "Error: message" / "Error message" without a file position.
    private val POSITIONLESS = Regex("""^(error|warning)\b[:]?\s*(.*)$""", RegexOption.IGNORE_CASE)

    /**
     * Parses a single raw ISCC output [rawLine] (already stripped of its trailing newline).
     */
    fun parseLine(rawLine: String): Line {
        val text = rawLine.trimEnd()

        val trimmed = text.trim()

        POSITIONAL.matchEntire(trimmed)?.let { m ->
            val (kind, lineStr, colStr, file, message) = m.destructured
            return Line.Problem(
                message = message.ifBlank { trimmed },
                detail = trimmed,
                file = file.trim(),
                line = lineStr.toIntOrNull()?.let { (it - 1).coerceAtLeast(0) },
                column = colStr.toIntOrNull()?.let { (it - 1).coerceAtLeast(0) },
                warning = kind.equals("warning", ignoreCase = true)
            )
        }

        POSITIONLESS.matchEntire(trimmed)?.let { m ->
            val (kind, message) = m.destructured
            return Line.Problem(
                message = message.ifBlank { trimmed },
                detail = trimmed,
                file = null,
                line = null,
                column = null,
                warning = kind.equals("warning", ignoreCase = true)
            )
        }

        return Line.Output(text)
    }
}

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
 * Stateful segmenter that turns the flat stream of ISCC output lines into [OutputSection]s — one
 * group per Inno-Setup section (`[Setup]`, `[Languages]`, …) plus catch-all groups for any output
 * that appears outside a section. Each completed section is reported through [onSection].
 *
 * It is deliberately free of any IntelliJ dependency so it can be unit-tested in isolation (mirrors
 * [IsBuildOutputParser]). The nested folding of indented detail lines is handled separately by the
 * console folding ([IsBuildOutputConsoleFolding]); this grouper only produces the top-level tree
 * grouping by section.
 */
class IsBuildOutputGrouper(private val onSection: (OutputSection) -> Unit) {

    /** A completed group of consecutive output lines. [title] already carries the `(N)` count. */
    data class OutputSection(val title: String, val lines: List<String>)

    /** Label of the currently open section (e.g. `[Setup]`), or `null` for a catch-all group. */
    private var currentLabel: String? = null
    private val buffer = mutableListOf<String>()

    /** Feeds a single raw output line (already stripped of its trailing newline). */
    fun accept(text: String) {
        val label = sectionLabel(text)
        if (label != null && label != currentLabel) {
            // A new section starts: flush whatever was buffered, then open the new section.
            flush()
            currentLabel = label
        }
        buffer.add(text)
    }

    /** Closes the currently open group, reporting it through [onSection] if it holds any line. */
    fun flush() {
        if (buffer.isEmpty()) {
            currentLabel = null
            return
        }
        val name = currentLabel ?: catchAllName(buffer.first())
        onSection(OutputSection("$name (${buffer.size})", buffer.toList()))
        buffer.clear()
        currentLabel = null
    }

    companion object {
        // "Parsing [Setup] section, line 10" → captures "[Setup]".
        private val SECTION = Regex("""^Parsing (\[[^]]+]) section\b.*""")

        /** Returns the `[X]` label when [text] is a `Parsing [X] section …` line, else `null`. */
        fun sectionLabel(text: String): String? = SECTION.matchEntire(text.trim())?.groupValues?.get(1)

        /** Short title for a catch-all group derived from its first line. */
        private fun catchAllName(firstLine: String): String = firstLine.trim()
    }
}

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

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem

/**
 * Console filter for the Inno Setup build output: turns the `line XX` token of an ISCC error/warning
 * line into a clickable hyperlink that opens the referenced file at that line.
 */
class IsBuildOutputFilter(private val project: Project) : Filter {

    /**
     * Highlights the `line XX` token of a positional ISCC problem line and links it to the file.
     */
    override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
        val parsed = IsBuildOutputParser.parseLine(line) as? IsBuildOutputParser.Line.Problem ?: return null
        val filePath = parsed.file ?: return null
        val docLine = parsed.line ?: return null
        val vFile = LocalFileSystem.getInstance().findFileByPath(filePath.replace('\\', '/')) ?: return null
        val token = LINE_TOKEN.find(line) ?: return null

        val lineStart = entireLength - line.length
        val info = OpenFileHyperlinkInfo(project, vFile, docLine, parsed.column ?: 0)
        return Filter.Result(lineStart + token.range.first, lineStart + token.range.last + 1, info)
    }

    companion object {
        private val LINE_TOKEN = Regex("""line\s+\d+""", RegexOption.IGNORE_CASE)
    }
}

/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.sections

/**
 * The **effective** script of an [IsScriptFile]: a fully `#include`-resolved, in-memory [IsScriptFile] whose
 * text is the original with every literal `#include "…"` line replaced by the (recursively resolved) text of
 * the referenced file.
 *
 * **Only `#include` is resolved.** No other ISPP directive is evaluated or expanded — `#define`, `#if`,
 * `#emit`, `#sub`, … are copied through verbatim. The effective file is purely the result of inlining
 * included file text; the preprocessor is not otherwise executed. Expression-based includes
 * (`#include MyVar`), angle-bracket / bare forms, missing files and include cycles are left untouched.
 *
 * The result is cached and recomputed whenever any PSI in the project changes. The original PSI is never
 * mutated. Reusable for validation, navigation and future tooling.
 */
fun IsScriptFile.toEffectiveScript(): IsScriptFile =
    CachedValuesManager.getCachedValue(this) {
        val visited = hashSetOf(virtualFile?.path ?: name)
        val merged = mergeIncludes(virtualFile?.parent, text, visited)
        val unified = mergeSameNamedSections(project, merged)
        val effective = PsiFileFactory.getInstance(project)
            .createFileFromText("effective_$name", IsScriptLanguage, unified) as IsScriptFile
        CachedValueProvider.Result.create(effective, PsiModificationTracker.MODIFICATION_COUNT)
    }

/**
 * Unifies sections that occur more than once (across the script and its now-inlined includes) into a single
 * section: the first occurrence keeps its header, the entries of every later same-named section are appended
 * to it and their headers dropped. Sections keep their first-occurrence order. Returns [text] unchanged when
 * there is nothing to merge.
 */
private fun mergeSameNamedSections(project: Project, text: String): String {
    val tmp = PsiFileFactory.getInstance(project)
        .createFileFromText("merge_tmp.iss", IsScriptLanguage, text) as? IsScriptFile ?: return text
    val blocks = tmp.sections
    if (blocks.isEmpty()) return text

    val grouped = LinkedHashMap<String, MutableList<IsSectionBlock>>()
    blocks.forEach { grouped.getOrPut(it.nameText.lowercase()) { mutableListOf() }.add(it) }
    if (grouped.values.none { it.size > 1 }) return text

    val sb = StringBuilder()
    // Preserve anything before the first section (e.g. leftover #define lines).
    sb.append(text, 0, blocks.first().textRange.startOffset)

    grouped.values.forEach { group ->
        sb.appendWithNewline(group.first().text)
        group.drop(1).forEach { block ->
            val headerLength = block.header.textRange.endOffset - block.textRange.startOffset
            val body = block.text.substring(headerLength).trimStart('\r', '\n')
            if (body.isNotBlank()) sb.appendWithNewline(body)
        }
    }
    return sb.toString()
}

/** Appends [s], guaranteeing it ends with a single newline so the next section starts on its own line. */
private fun StringBuilder.appendWithNewline(s: String) {
    append(s)
    if (!s.endsWith("\n")) append('\n')
}

/** A literal `#include "path"` line (the only form that is inlined; quoted path captured in group 1). */
private val LITERAL_INCLUDE =
    Regex("""(?m)^[ \t]*#[ \t]*include[ \t]+"([^"\r\n]+)"[ \t]*\r?$""", RegexOption.IGNORE_CASE)

/**
 * Replaces each literal `#include` line in [text] with the recursively merged text of its target. [visited]
 * holds the canonical paths already inlined on the current path, guarding against cycles and double inclusion.
 */
private fun mergeIncludes(baseDir: VirtualFile?, text: String, visited: MutableSet<String>): String {
    if (baseDir == null) return text
    return LITERAL_INCLUDE.replace(text) { match ->
        val target = IsIncludePaths.resolve(baseDir, match.groupValues[1]) ?: return@replace match.value
        if (!visited.add(target.path)) return@replace match.value
        val content = runCatching { VfsUtilCore.loadText(target) }.getOrNull() ?: return@replace match.value
        try {
            mergeIncludes(target.parent, content, visited)
        } finally {
            visited.remove(target.path)
        }
    }
}

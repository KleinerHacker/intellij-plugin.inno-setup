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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.isppDirectivesWithHostOffset
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionBlock
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
        CachedValueProvider.Result.create(buildOrReuseEffective(this), PsiModificationTracker.MODIFICATION_COUNT)
    }

/** Per-modification-count memo of the merged effective file, so recomputation returns the same instance. */
private val EFFECTIVE_SCRIPT_INSTANCE: Key<Pair<Long, IsScriptFile>> = Key.create("inno.effectiveScript.instance")

/**
 * Builds the merged effective [IsScriptFile], reusing the instance already produced for the current PSI
 * modification count. Creating a fresh PSI file on every call would make [toEffectiveScript]'s cached value
 * non-idempotent (IntelliJ's idempotence checker compares two computations and would see two distinct files);
 * memoizing per modification count keeps repeated computations stable.
 */
private fun buildOrReuseEffective(host: IsScriptFile): IsScriptFile {
    val stamp = PsiModificationTracker.getInstance(host.project).modificationCount
    host.getUserData(EFFECTIVE_SCRIPT_INSTANCE)?.let { if (it.first == stamp) return it.second }

    val visited = hashSetOf(host.virtualFile?.path ?: host.name)
    val merged = mergeIncludes(host.virtualFile?.parent, host.text, visited)
    val unified = mergeSameNamedSections(host.project, merged)
    val effective = PsiFileFactory.getInstance(host.project)
        .createFileFromText("effective_${host.name}", IsScriptLanguage, unified) as IsScriptFile
    // Mark it so a (re-entrant) declaration lookup on the effective file short-circuits to itself
    // instead of building yet another effective script (see [declarationScope]).
    effective.putUserData(EFFECTIVE_SCRIPT_MARKER, true)
    host.putUserData(EFFECTIVE_SCRIPT_INSTANCE, stamp to effective)
    return effective
}

/**
 * The script that *declarations* must be resolved against during validation: the fully `#include`-resolved
 * effective script, so that sections, `#define`s, custom messages and language names contributed by included
 * files are taken into account (otherwise a value that is only provided through an `#include` is wrongly
 * reported as missing/unknown).
 *
 * For a file that already **is** an effective script (e.g. while the effective-script problem replay walks it)
 * this returns the file itself, avoiding needless recomputation and any re-entrancy.
 */
fun IsScriptFile.declarationScope(): IsScriptFile =
    if (getUserData(EFFECTIVE_SCRIPT_MARKER) == true) this else toEffectiveScript()

/**
 * Marker set on an in-memory effective [IsScriptFile]. The annotators recognise it to avoid re-triggering the
 * effective-script analysis recursively when they walk the effective file (see `IsPreprocessorAnnotator`).
 */
val EFFECTIVE_SCRIPT_MARKER: Key<Boolean> = Key.create("inno.effectiveScript")

/**
 * One contiguous slice of the attributed effective text together with where it came from: [origin] is `null`
 * for text of the host (main) file, or the **topmost** `#include` directive of the host file through which the
 * slice was pulled in (no matter how deeply nested the actual include is).
 */
data class IsEffectiveSegment(val range: TextRange, val origin: SmartPsiElementPointer<IsPreprocessorDirective>?)

/**
 * The effective, fully `#include`-resolved text of a script **without** same-named section merging (so output
 * offsets map 1:1 onto the source slices) plus a [segments] source-map attributing every offset to its origin.
 */
class IsAttributedEffectiveScript(val file: IsScriptFile, val segments: List<IsEffectiveSegment>) {
    /** The topmost host `#include` directive that produced the text at [offset], or `null` for host text. */
    fun originAt(offset: Int): SmartPsiElementPointer<IsPreprocessorDirective>? =
        segments.firstOrNull { offset >= it.range.startOffset && offset < it.range.endOffset }?.origin
}

/**
 * Builds the [IsAttributedEffectiveScript] of this host file: the host text with every literal `#include "…"`
 * line replaced by the (recursively resolved) text of its target, each produced slice attributed to the host
 * `#include` that introduced it. Unlike [toEffectiveScript] no section merge is applied, so segment offsets map
 * exactly. Cached and recomputed on any PSI change. Returns `null` when the host has no literal `#include`.
 */
fun IsScriptFile.toAttributedEffectiveScript(): IsAttributedEffectiveScript? =
    CachedValuesManager.getCachedValue(this) {
        CachedValueProvider.Result.create(buildOrReuseAttributed(this), PsiModificationTracker.MODIFICATION_COUNT)
    }

/** Per-modification-count memo of the attributed effective script (see [buildOrReuseEffective] for why). */
private val ATTRIBUTED_EFFECTIVE_INSTANCE: Key<Pair<Long, IsAttributedEffectiveScript?>> =
    Key.create("inno.effectiveScript.attributed.instance")

/**
 * Builds the [IsAttributedEffectiveScript], reusing the instance already produced for the current PSI
 * modification count so [toAttributedEffectiveScript]'s cached value stays idempotent (it wraps a freshly
 * parsed PSI file, which would otherwise differ between two computations).
 */
private fun buildOrReuseAttributed(host: IsScriptFile): IsAttributedEffectiveScript? {
    val stamp = PsiModificationTracker.getInstance(host.project).modificationCount
    host.getUserData(ATTRIBUTED_EFFECTIVE_INSTANCE)?.let { if (it.first == stamp) return it.second }
    val built = buildAttributedEffectiveScript(host)
    host.putUserData(ATTRIBUTED_EFFECTIVE_INSTANCE, stamp to built)
    return built
}

private fun buildAttributedEffectiveScript(host: IsScriptFile): IsAttributedEffectiveScript? {
    val hostText = host.text
    if (!LITERAL_INCLUDE.containsMatchIn(hostText)) return null

    val baseDir = host.virtualFile?.parent
    val pointerManager = SmartPointerManager.getInstance(host.project)
    // The host's literal #include directives paired with the start offset of their line — the anchor used to
    // attribute a regex match back to the directive that owns it.
    val includeAnchors = host.isppDirectivesWithHostOffset
        .filter { (d, _) -> (d as? IsPreprocessorDirectiveEx)?.isInclude() == true }

    val visited = hashSetOf(host.virtualFile?.path ?: host.name)
    val segments = mutableListOf<IsEffectiveSegment>()
    val sb = StringBuilder()

    fun append(text: String, origin: SmartPsiElementPointer<IsPreprocessorDirective>?) {
        if (text.isEmpty()) return
        val start = sb.length
        sb.append(text)
        segments += IsEffectiveSegment(TextRange(start, sb.length), origin)
    }

    var lastEnd = 0
    LITERAL_INCLUDE.findAll(hostText).forEach { match ->
        if (match.range.first > lastEnd) {
            append(hostText.substring(lastEnd, match.range.first), null)
        }
        val directive = includeAnchors.firstOrNull { it.second in match.range }?.first
        val origin = directive?.let { pointerManager.createSmartPsiElementPointer(it) }

        val target = baseDir?.let { IsIncludePaths.resolve(it, match.groupValues[1]) }
        val expanded = when {
            target == null -> match.value                                   // unresolvable → keep verbatim
            !visited.add(target.path) -> match.value                        // cycle → keep verbatim
            else -> {
                val content = runCatching { VfsUtilCore.loadText(target) }.getOrNull()
                if (content == null) match.value
                else try {
                    mergeIncludes(target.parent, content, visited)
                } finally {
                    visited.remove(target.path)
                }
            }
        }
        append(expanded, origin)
        lastEnd = match.range.last + 1
    }
    if (lastEnd < hostText.length) append(hostText.substring(lastEnd), null)

    val effective = PsiFileFactory.getInstance(host.project)
        .createFileFromText("attributed_${host.name}", IsScriptLanguage, sb.toString()) as IsScriptFile
    effective.putUserData(EFFECTIVE_SCRIPT_MARKER, true)
    return IsAttributedEffectiveScript(effective, segments)
}

/**
 * The read-only *effective script view* of a host file: the fully `#include`-resolved [text] together with the
 * [includeRanges] (offsets into [text]) that originate from an `#include`. Used by the effective-script viewer
 * to render the merged script with the include-pulled parts tinted. When the host has no literal `#include`,
 * [text] is the original and [includeRanges] is empty.
 */
data class IsEffectiveScriptView(val text: String, val includeRanges: List<TextRange>)

/**
 * Builds the [IsEffectiveScriptView] of this host file from its [toAttributedEffectiveScript] (offsets map 1:1,
 * since the attributed variant applies no same-named section merge).
 */
fun IsScriptFile.effectiveScriptView(): IsEffectiveScriptView {
    val attributed = toAttributedEffectiveScript() ?: return IsEffectiveScriptView(text, emptyList())
    val ranges = attributed.segments.filter { it.origin != null }.map { it.range }
    return IsEffectiveScriptView(attributed.file.text, ranges)
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

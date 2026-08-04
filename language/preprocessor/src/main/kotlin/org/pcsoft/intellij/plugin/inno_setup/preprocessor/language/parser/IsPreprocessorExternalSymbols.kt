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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import java.util.concurrent.ConcurrentHashMap

/**
 * The externally contributed preprocessor symbols of one script, cached until the
 * [IsPreprocessorSymbolTracker] moves.
 *
 * The symbols come from [IsPreprocessorSymbolProvider] — in practice the `/D<name>[=<value>]` arguments of
 * the build configuration behind the selected run configuration. They are consumed on every re-analysis of a
 * file (branch analysis, annotators, completion, type resolution), so collecting them is cached per script:
 * the providers themselves are cheap, but reading a build configuration once per `{#…}` constant of a large
 * script is not.
 *
 * Names are compared case-insensitively, exactly like ISPP compares `#define` names.
 */
@Service(Service.Level.PROJECT)
class IsPreprocessorExternalSymbols(private val project: Project) {

    private data class Entry(val stamp: Long, val symbols: Map<String, String?>)

    // Keyed by the script's URL (null-safe placeholder for an in-memory file without a backing document).
    private val cache = ConcurrentHashMap<String, Entry>()

    /**
     * The symbols in effect for [script], mapped name → value. A `null` value means the symbol is defined but
     * carries no usable value (`/DDEBUG`).
     */
    fun symbolsOf(script: VirtualFile?): Map<String, String?> {
        val stamp = IsPreprocessorSymbolTracker.getInstance(project).modificationCount
        val key = script?.url ?: NO_FILE_KEY
        cache[key]?.takeIf { it.stamp == stamp }?.let { return it.symbols }

        val symbols = IsPreprocessorSymbolProvider.collect(project, script)
        cache[key] = Entry(stamp, symbols)
        return symbols
    }

    /** Whether [name] is contributed from outside for [script] (case-insensitive). */
    fun isDefined(script: VirtualFile?, name: String): Boolean =
        symbolsOf(script).keys.any { it.equals(name, ignoreCase = true) }

    /** The value contributed for [name], or `null` when it is undefined or carries no value. */
    fun valueOf(script: VirtualFile?, name: String): String? =
        symbolsOf(script).entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value

    companion object {

        private const val NO_FILE_KEY = "<no-file>"

        /** Returns the external-symbol cache of [project]. */
        fun getInstance(project: Project): IsPreprocessorExternalSymbols =
            project.getService(IsPreprocessorExternalSymbols::class.java)
    }
}

/**
 * The [VirtualFile] whose external symbols apply to this (possibly in-memory) file.
 *
 * An effective script built in memory has no `VirtualFile` of its own; [EFFECTIVE_SCRIPT_ORIGIN] points at
 * the script it was built from, so it sees the same `/D…` symbols the editor uses on the original.
 */
fun PsiFile.externalSymbolScript(): VirtualFile? =
    getUserData(EFFECTIVE_SCRIPT_ORIGIN) ?: virtualFile ?: originalFile.virtualFile

/** The externally contributed preprocessor symbols in effect for this file, mapped name → value. */
fun PsiFile.externalPreprocessorSymbols(): Map<String, String?> =
    IsPreprocessorExternalSymbols.getInstance(project).symbolsOf(externalSymbolScript())

/**
 * Whether [name] is an externally contributed preprocessor symbol of this file (case-insensitive).
 *
 * Such a symbol has no declaration inside the script, so it can never resolve to a PSI element — but it *is*
 * defined for the build, and reporting it as unresolved would flag a perfectly valid script.
 */
fun PsiFile.isExternalPreprocessorSymbol(name: String): Boolean =
    IsPreprocessorExternalSymbols.getInstance(project).isDefined(externalSymbolScript(), name)

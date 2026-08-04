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

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * Contributes preprocessor symbols that are **not** declared in the script itself but are handed to ISCC from
 * outside — in practice the `/D<name>[=<value>]` arguments of a run configuration.
 *
 * Without such a contribution the very common `#ifdef DEBUG` pattern can never be decided, because the symbol
 * exists only at compile time; the branch analysis would have to mark it undecidable and the editor would show
 * a "not evaluable" marker on virtually every build-flag condition.
 *
 * The extension point lives in the lowest module so the analysis can consume it, while the implementation that
 * knows about run configurations lives in the plugin module — the same interface-down / implementation-up
 * split [IsPreprocessorHost] uses for the script host.
 */
interface IsPreprocessorSymbolProvider {

    /**
     * Symbols in effect for [script], mapped name → value. A `null` value means the symbol is defined but
     * carries no usable value (`/DDEBUG`), which makes `#ifdef DEBUG` decidable while `#if DEBUG > 2` stays
     * undecidable. [script] is `null` for an in-memory file that has no backing document.
     *
     * Implementations must be side-effect free and fast: this runs inside a cached read action on every
     * re-analysis of the file.
     */
    fun symbols(project: Project, script: VirtualFile?): Map<String, String?>

    companion object {
        /** The extension point contributing externally defined preprocessor symbols. */
        val EP_NAME: ExtensionPointName<IsPreprocessorSymbolProvider> =
            ExtensionPointName.create("org.pcsoft.intellij.plugin.inno_setup.preprocessorSymbolProvider")

        /**
         * The merged symbols of every registered provider for [script], mapped name → value.
         *
         * A provider that throws is skipped rather than breaking the analysis of the whole file; later
         * providers win over earlier ones for the same name. Callers that run per file element should go
         * through [IsPreprocessorExternalSymbols] instead, which caches this result.
         */
        fun collect(project: Project, script: VirtualFile?): Map<String, String?> {
            val symbols = mutableMapOf<String, String?>()
            EP_NAME.extensionList.forEach { provider ->
                runCatching { provider.symbols(project, script) }.getOrNull()?.let { symbols.putAll(it) }
            }
            return symbols
        }
    }
}

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

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.Key
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective

/**
 * Host-side abstraction the ISPP preprocessor module talks to, instead of depending on the concrete
 * script/section/template PSI of the [:language:script] module. The host files that ISPP `#…` fragments are
 * injected into (the `.iss`/`.isl` script and the `.ist` template) implement this interface and supply the
 * few host-aware behaviours the preprocessor needs. This keeps the dependency direction
 * `script → preprocessor` and the preprocessor module free of any section/script type.
 */
interface IsPreprocessorHost {

    /**
     * Whether the `#define`/`#dim` named [name] is referenced as a `{#Name}` constant anywhere in this host
     * file. Used to decide whether a `#define` is actually used. The default (no such usage) is correct for
     * hosts without section constants (e.g. a `.ist` template); the script host overrides it.
     */
    fun isPreprocessorNameReferencedAsConstant(name: String): Boolean = false

    /**
     * Problems that *including* the file referenced by [directive] introduces into the combined effective
     * script (flag conflicts, context-only fragments, transitively missing includes, …). Only the script host
     * performs this combined analysis; other hosts report none.
     */
    fun includeProblems(directive: IsPreprocessorDirective): List<IsPreprocessorIncludeProblem> = emptyList()
}

/**
 * Marker for a host line element that carries an injected ISPP `#…` fragment (a section preprocessor line of a
 * script or the preprocessor line of a template). The preprocessor enumerates these without knowing the
 * concrete host grammar.
 */
interface IsPreprocessorHostLine

/** A single problem surfaced on an `#include`, decoupled from the script-side effective-script analysis. */
data class IsPreprocessorIncludeProblem(val severity: HighlightSeverity, val message: String)

/**
 * Marks the in-memory *effective script* (the script-module builds it by inlining every `#include`). The ISPP
 * annotator checks this key to stop the effective-script analysis from recursing when it replays over the
 * effective file. Defined here (the lowest module) so both the annotator and the script-side builder share it.
 */
val EFFECTIVE_SCRIPT_MARKER: Key<Boolean> = Key.create("inno.effectiveScript")

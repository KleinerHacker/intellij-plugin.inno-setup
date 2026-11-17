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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.version

import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService

/**
 * Shared version logic for ISPP directives, built-in functions and predefined variables.
 *
 * It compares the `since`/`until` values of the preprocessor specification with the configured minimum Inno
 * Setup version. It lives in the `plugin` module because the minimum version is a script-module setting, which
 * the independent `preprocessor` module must not know about.
 */
object IsPreprocessorVersionSupport {

    /** Availability of a spec entry for the configured minimum version. */
    enum class State {
        /** The entry is available (or no minimum version is configured). */
        AVAILABLE,

        /** The entry was introduced after the configured minimum version. */
        TOO_NEW,

        /** The entry was removed up to the configured minimum version. */
        REMOVED
    }

    /** A spec entry (function or variable) found in an expression, with its position inside the scanned text. */
    data class Usage(val name: String, val range: TextRange, val since: String?, val until: String?)

    private val IDENTIFIER = Regex("[A-Za-z_][A-Za-z0-9_]*")
    private val STRING_LITERAL = Regex("\"[^\"]*\"")

    /**
     * Returns the availability of an entry with the given [since]/[until] for [minVersion]
     * (defaults to the configured minimum version).
     */
    fun stateOf(
        since: String?,
        until: String?,
        minVersion: String? = IsSettingsService.getInstance().state.minInnoVersion
    ): State {
        if (minVersion == null) return State.AVAILABLE
        if (until != null && IsSettingsService.compareIsVersions(until, minVersion) <= 0) return State.REMOVED
        if (since != null && IsSettingsService.compareIsVersions(since, minVersion) > 0) return State.TOO_NEW
        return State.AVAILABLE
    }

    /** Completion tail-text hint of an entry, e.g. ` [IS 7.1+]`, or an empty string when it is available. */
    fun hint(since: String?, until: String?): String = when (stateOf(since, until)) {
        State.REMOVED -> " [removed IS $until]"
        State.TOO_NEW -> " [IS $since+]"
        State.AVAILABLE -> ""
    }

    /** The warning/error message for an unavailable entry, or `null` when the entry is available. */
    fun message(displayName: String, since: String?, until: String?): String? {
        val minVersion = IsSettingsService.getInstance().state.minInnoVersion
        return when (stateOf(since, until, minVersion)) {
            State.REMOVED -> "'$displayName' was removed in Inno Setup $until"
            State.TOO_NEW -> "'$displayName' requires Inno Setup $since or later (configured minimum: $minVersion)"
            State.AVAILABLE -> null
        }
    }

    /**
     * Finds the built-in functions (followed by an opening parenthesis) and the predefined variables of the ISPP
     * specification that are written in [expression]. String literals are skipped. Ranges are relative to the
     * start of [expression] shifted by [offset].
     */
    fun findUsages(expression: String, offset: Int = 0): List<Usage> {
        val spec = service<IsPreprocessorService>().spec
        val blanked = STRING_LITERAL.replace(expression) { " ".repeat(it.value.length) }
        return IDENTIFIER.findAll(blanked).mapNotNull { match ->
            val name = match.value
            val isCall = blanked.substring(match.range.last + 1).trimStart().startsWith("(")
            val (since, until) = if (isCall) {
                spec.builtinFunctions.firstOrNull { it.name.equals(name, ignoreCase = true) }
                    ?.let { it.since to it.until }
            } else {
                spec.predefinedVariables.firstOrNull { it.name.equals(name, ignoreCase = true) }
                    ?.let { it.since to it.until }
            } ?: return@mapNotNull null
            if (since == null && until == null) return@mapNotNull null
            Usage(name, TextRange(offset + match.range.first, offset + match.range.last + 1), since, until)
        }.toList()
    }
}

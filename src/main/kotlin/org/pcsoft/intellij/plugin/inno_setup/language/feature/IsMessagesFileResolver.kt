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

package org.pcsoft.intellij.plugin.inno_setup.language.feature

import org.pcsoft.intellij.plugin.inno_setup.language.feature.IsMessagesFileResolver.expandValue
import java.io.File

/** Result of resolving a `MessagesFile` value to a filesystem path. */
sealed interface IsResolveResult {
    /** The file exists and is readable. */
    data class Ok(val file: File) : IsResolveResult

    /** The resolved path does not exist on disk. */
    data class Missing(val resolvedPath: String) : IsResolveResult

    /** The path exists but cannot be read. */
    data class Unreadable(val resolvedPath: String) : IsResolveResult

    /** A `compiler:` prefix was found but the Inno Setup installation path is not configured. */
    object NotConfigured : IsResolveResult

    /** The value still contains unresolved `{…}` placeholders and cannot be checked. */
    object Unresolvable : IsResolveResult
}

/**
 * Pure-logic utilities for expanding and resolving `MessagesFile` values in `[Languages]` sections.
 * All methods are stateless and testable without a PSI/project context.
 */
object IsMessagesFileResolver {

    /**
     * Expands all `{…}` placeholders in [raw] and returns the result string, or `null` if any
     * placeholder cannot be resolved (which means the path is not checkable — no error should
     * be emitted).
     *
     * @param raw           Raw `MessagesFile` value, possibly containing `{#Foo}`, `{%ENV}`, or
     *                      built-in constants.
     * @param defines       ISPP `#define` name→value pairs from the host `.iss` file.
     * @param scriptDir     Directory of the `.iss` file, used to resolve `{src}` and `{#SourcePath}`.
     * @param customMessages Entries from `[CustomMessages]` key→value (optional, rarely used in paths).
     * @param installPath   Inno Setup installation directory (from settings), used for `{#CompilerPath}`.
     */
    fun expandValue(
        raw: String,
        defines: List<Pair<String, String?>>,
        scriptDir: File?,
        customMessages: Map<String, String?> = emptyMap(),
        installPath: String? = null
    ): String? {
        val sb = StringBuilder()
        var i = 0
        while (i < raw.length) {
            val brace = raw.indexOf('{', i)
            if (brace < 0) {
                sb.append(raw.substring(i))
                break
            }
            sb.append(raw.substring(i, brace))
            val close = raw.indexOf('}', brace + 1)
            if (close < 0) return null  // unclosed brace — treat as unresolvable
            val inner = raw.substring(brace + 1, close)
            val expanded = expandPlaceholder(inner, defines, scriptDir, customMessages, installPath) ?: return null
            sb.append(expanded)
            i = close + 1
        }
        return sb.toString()
    }

    private fun expandPlaceholder(
        inner: String,
        defines: List<Pair<String, String?>>,
        scriptDir: File?,
        customMessages: Map<String, String?>,
        installPath: String?
    ): String? = when {
        // {#Name} — ISPP define, with fallback to a value-bearing predefined variable
        inner.startsWith("#") -> {
            val name = inner.removePrefix("#").trim()
            val entry = defines.firstOrNull { it.first.equals(name, ignoreCase = true) }
            if (entry != null) entry.second  // null if the define has no value (function macro)
            else resolvePredefinedVariable(name, scriptDir, installPath)
        }

        // {%ENV} or {%ENV|default}
        inner.startsWith("%") -> {
            val rest = inner.removePrefix("%")
            val pipe = rest.indexOf('|')
            val envName = if (pipe >= 0) rest.substring(0, pipe) else rest
            val default = if (pipe >= 0) rest.substring(pipe + 1) else null
            System.getenv(envName) ?: default
        }

        // {cm:Name} — custom message reference (uncommon in paths, but consistent)
        inner.startsWith("cm:", ignoreCase = true) -> {
            val msgName = inner.substring(3).substringBefore(',').trim()
            customMessages.entries.firstOrNull { it.key.equals(msgName, ignoreCase = true) }?.value
        }

        // Built-in directory constant — delegate to OS-aware resolver
        else -> {
            val name = inner.substringBefore(':').trim()
            IsConstantResolver.resolve(name, scriptDir)
        }
    }

    /**
     * Resolves a value-bearing ISPP predefined variable to a statically known path component, or
     * `null` when its value is only known at compile time (`__LINE__`, `Ver`, …) or is not a path
     * (`NewLine`, `Tab`). Only the path-relevant variables are mapped; everything else stays
     * unresolvable so no false error is produced. `void` symbols never reach here (they carry no value).
     */
    private fun resolvePredefinedVariable(name: String, scriptDir: File?, installPath: String?): String? =
        when (name.lowercase()) {
            "sourcepath", "__dir__" -> scriptDir?.absolutePath
            "compilerpath" -> installPath?.ifBlank { null }
            "syspath" -> IsConstantResolver.resolve("sys", scriptDir)
            else -> null
        }

    /**
     * Resolves an already-expanded (no `{…}` remaining) `MessagesFile` path to an [IsResolveResult].
     *
     * @param expandedValue Path with all placeholders already resolved by [expandValue].
     * @param scriptDir     Directory of the `.iss` file for relative path resolution.
     * @param installPath   Inno Setup installation directory (from settings), used for `compiler:` paths.
     */
    fun resolveMessagesFile(expandedValue: String, scriptDir: File?, installPath: String?): IsResolveResult {
        val normalized = expandedValue.replace('\\', '/')
        val file: File = when {
            normalized.startsWith("compiler:", ignoreCase = true) -> {
                if (installPath.isNullOrBlank()) return IsResolveResult.NotConfigured
                val rest = normalized.substring("compiler:".length).replace('/', File.separatorChar)
                File(installPath, rest)
            }

            File(normalized).isAbsolute -> File(normalized)

            else -> {
                if (scriptDir == null) return IsResolveResult.Unresolvable
                File(scriptDir, normalized.replace('/', File.separatorChar))
            }
        }

        return when {
            !file.exists() -> IsResolveResult.Missing(file.absolutePath)
            !file.canRead() -> IsResolveResult.Unreadable(file.absolutePath)
            else -> IsResolveResult.Ok(file)
        }
    }
}

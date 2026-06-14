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

import org.pcsoft.intellij.plugin.inno_setup.language.feature.IsMessagesFileResolver
import org.pcsoft.intellij.plugin.inno_setup.language.feature.IsResolveResult
import java.io.File
import java.security.MessageDigest

/**
 * Computes an MD5 hash over all files that participate in compiling a top-level `.iss` script:
 * the script itself, every `.iss` pulled in (recursively) via `#include`, and every `.isl` language
 * file referenced through a `[Languages] MessagesFile:` entry that resolves to a local file.
 *
 * The hash is used to decide whether a recompile is required before launching the installer
 * ([org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunProcessHandler]) and to skip unchanged
 * scripts during the regular project build ([IsCompilerService]).
 *
 * All functions operate purely on the filesystem (no PSI/project), so they are unit-testable.
 */
object IsScriptHasher {

    // MessagesFile: "path" | MessagesFile: path  (within [Languages] entries)
    private val MESSAGES_FILE = Regex(
        """MessagesFile\s*:\s*(?:"([^"]+)"|([^;\r\n]+))""",
        RegexOption.IGNORE_CASE
    )

    /**
     * Collects every file participating in the compilation of [topLevelScript]. Returns a distinct,
     * existing-file list (the script first, then includes and `.isl` references). Missing or
     * unresolvable references are silently skipped.
     *
     * @param installPath Inno Setup installation directory, used to resolve `compiler:` `.isl` paths.
     */
    fun collectParticipatingFiles(topLevelScript: File, installPath: String?): List<File> {
        val result = LinkedHashMap<String, File>()
        val pending = ArrayDeque<File>()
        pending.add(topLevelScript)

        while (pending.isNotEmpty()) {
            val script = pending.removeFirst()
            val key = canonical(script)
            if (result.containsKey(key)) continue
            if (!script.isFile) continue
            result[key] = script

            val text = runCatching { script.readText() }.getOrNull() ?: continue
            val scriptDir = script.parentFile

            // Nested #include of further .iss fragments.
            for (raw in IsScriptCollector.parseIncludes(text)) {
                resolveRelative(scriptDir, raw)?.let { pending.add(it) }
            }

            // Referenced .isl language files.
            for (isl in collectLanguageFiles(text, scriptDir, installPath)) {
                result.putIfAbsent(canonical(isl), isl)
            }
        }

        return result.values.toList()
    }

    /**
     * Convenience: collect participating files and hash them in one step.
     */
    fun hashScript(topLevelScript: File, installPath: String?): String =
        hashFiles(collectParticipatingFiles(topLevelScript, installPath))

    /**
     * Computes a stable MD5 hex digest over [files]. The files are sorted by canonical path so the
     * result is independent of collection order; each file contributes its path plus its raw bytes.
     * Unreadable files contribute their path only, keeping the digest deterministic.
     */
    fun hashFiles(files: List<File>): String {
        val digest = MessageDigest.getInstance("MD5")
        for (file in files.sortedBy { canonical(it) }) {
            digest.update(canonical(file).toByteArray(Charsets.UTF_8))
            digest.update(0)
            runCatching { file.readBytes() }.getOrNull()?.let { digest.update(it) }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun collectLanguageFiles(text: String, scriptDir: File?, installPath: String?): List<File> =
        MESSAGES_FILE.findAll(text).mapNotNull { m ->
            val raw = (m.groupValues[1].takeIf { it.isNotEmpty() } ?: m.groupValues[2]).trim()
            if (raw.isEmpty()) return@mapNotNull null
            val expanded = IsMessagesFileResolver.expandValue(raw, emptyList(), scriptDir) ?: return@mapNotNull null
            (IsMessagesFileResolver.resolveMessagesFile(expanded, scriptDir, installPath) as? IsResolveResult.Ok)?.file
        }.toList()

    private fun resolveRelative(baseDir: File?, path: String): File? {
        val file = File(path)
        return if (file.isAbsolute) file else baseDir?.let { File(it, path) }
    }

    private fun canonical(file: File): String =
        runCatching { file.canonicalPath }.getOrDefault(file.absolutePath)
}

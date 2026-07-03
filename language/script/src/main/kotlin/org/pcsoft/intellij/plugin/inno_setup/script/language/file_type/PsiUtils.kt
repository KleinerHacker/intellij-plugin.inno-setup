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

package org.pcsoft.intellij.plugin.inno_setup.script.language.file_type

import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.findSections
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.valueText
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsLanguageDataService
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import java.io.File
import java.util.concurrent.ConcurrentHashMap

// ── PsiElement (Host) ──────────────────────────────────────────────────────────

/**
 * Returns or performs the public behavior represented by this member.
 */
val PsiElement.issFile: IsScriptFile?
    get() = containingFile as? IsScriptFile

// ── IsScriptFile (Language) ──────────────────────────────────────────────────────────

private val LANGUAGE_ID_REGEX = Regex("^LanguageID\\s*=\\s*([^\\s;]+)", RegexOption.IGNORE_CASE)

private val COMMON_INNO_DIRS = listOf(
    "C:\\Program Files (x86)\\Inno Setup 6",
    "C:\\Program Files\\Inno Setup 6",
)

private data class Cached(val stamp: Long, val id: Int?)

private val cache = ConcurrentHashMap<String, Cached>()

/** Numeric LCID for [messagesFile] (already unquoted), or `null` when it cannot be determined. */
fun IsScriptFile?.languageId(messagesFile: String): Int? {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val mf = messagesFile.trim().removeSurrounding("\"").trim()
    if (mf.isEmpty()) return null
    parseResolved(mf, this)?.let { return it }
    // Offline fallback: the bundled MessagesFile → LCID mapping.
    return service<IsLanguageDataService>().fromMessagesFile(mf)
        ?.let { IsLanguageDataService.parseId(it.id) }
}

private fun parseResolved(messagesFile: String, context: IsScriptFile?): Int? =
    if (messagesFile.startsWith("compiler:", ignoreCase = true)) {
        val rel = messagesFile.substring("compiler:".length).replace('\\', '/')
        innoDir?.let { readDisk(File(it, rel)) }
    } else {
        resolveProjectFile(messagesFile, context)?.let { readVfs(it) }
    }

private val innoDir: String?
    get() {
        val configured = IsSettingsService.getInstance().state.installationPath
        if (!configured.isNullOrBlank() && File(configured).isDirectory) return configured
        return COMMON_INNO_DIRS.firstOrNull { File(it).isDirectory }
    }

private fun resolveProjectFile(messagesFile: String, context: IsScriptFile?): VirtualFile? {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val scriptDir = context?.virtualFile?.parent ?: return null

    /**
     * Returns or performs the public behavior represented by this member.
     */
    val sourceDir = sourceDirOf(context)

    /**
     * Returns or performs the public behavior represented by this member.
     */
    val base = sourceDir?.let { scriptDir.findFileByRelativePath(it) } ?: scriptDir
    return base.findFileByRelativePath(messagesFile.replace('\\', '/'))
}

/** The relative `\[Setup] SourceDir`, or `null` for the default (the script's own directory). */
private fun sourceDirOf(file: IsScriptFile): String? {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val dir = file.findSections("Setup").firstOrNull()
        ?.directiveEntryList?.firstOrNull { it.keyText().equals("SourceDir", ignoreCase = true) }
        ?.valueText?.trim()?.removeSurrounding("\"")?.trim()
    // Absolute SourceDir is not supported here; '.' / empty means the script directory.
    return dir?.takeIf { it.isNotEmpty() && it != "." && !it.contains(':') && !it.startsWith("\\") }
}

private fun readDisk(file: File): Int? {
    if (!file.isFile) return null
    return cached(file.absolutePath, file.lastModified()) {
        runCatching { extractLanguageId(file.readText(Charsets.ISO_8859_1)) }.getOrNull()
    }
}

private fun readVfs(vf: VirtualFile): Int? {
    if (!vf.isValid || vf.isDirectory) return null
    return cached(vf.url, vf.modificationStamp) {
        runCatching { extractLanguageId(VfsUtilCore.loadText(vf)) }.getOrNull()
    }
}

private inline fun cached(key: String, stamp: Long, compute: () -> Int?): Int? {
    cache[key]?.let { if (it.stamp == stamp) return it.id }
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val id = compute()
    cache[key] = Cached(stamp, id)
    return id
}

/** Scans a `.isl`/`.iss` text for the `LanguageID` value inside its \[LangOptions] section. */
private fun extractLanguageId(text: String): Int? {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    var inLangOptions = false
    for (raw in text.lineSequence()) {
        val line = raw.trim()
        if (line.startsWith("[")) {
            inLangOptions = line.equals("[LangOptions]", ignoreCase = true)
            continue
        }
        if (inLangOptions) {
            val m = LANGUAGE_ID_REGEX.find(line) ?: continue
            return IsLanguageDataService.parseId(m.groupValues[1].trim())
        }
    }
    return null
}

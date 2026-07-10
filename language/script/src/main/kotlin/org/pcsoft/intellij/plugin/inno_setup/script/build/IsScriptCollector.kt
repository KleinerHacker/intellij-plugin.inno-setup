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

package org.pcsoft.intellij.plugin.inno_setup.script.build

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import org.pcsoft.intellij.plugin.inno_setup.script.build.IsScriptCollector.Companion.parseIncludes
import java.io.File

/**
 * Finds the `.iss` scripts in a project that should be compiled on build: the **top-level** scripts,
 * i.e. those not pulled in via `#include` from another `.iss`. Include fragments would fail to
 * compile standalone, so they are excluded.
 *
 * The `#include` extraction ([parseIncludes]) is a pure helper for unit testing.
 */
class IsScriptCollector(private val project: Project) {

    /**
     * Returns every `.iss` file indexed in the project.
     */
    fun allScripts(): List<VirtualFile> =
        ApplicationManager.getApplication().runReadAction(Computable {
            FilenameIndex.getAllFilesByExt(project, "iss", GlobalSearchScope.projectScope(project)).toList()
        })

    /**
     * Returns the top-level scripts (all scripts minus those included by another script).
     */
    fun collectTopLevel(): List<VirtualFile> {
        val included = includedPaths()
        return allScripts().filterNot { norm(it.path) in included }
    }

    /**
     * Returns whether [file] is pulled in via `#include` by any script in the project.
     */
    fun isIncludedByOther(file: VirtualFile): Boolean = norm(file.path) in includedPaths()

    /** Canonical paths of every `.iss` referenced by a `#include` anywhere in the project. */
    private fun includedPaths(): Set<String> {
        val result = HashSet<String>()
        for (script in allScripts()) {
            val baseDir = script.parent?.path ?: continue
            val text = runCatching { VfsUtilCore.loadText(script) }.getOrNull() ?: continue
            for (raw in parseIncludes(text)) {
                resolveInclude(baseDir, raw)?.let { result.add(it) }
            }
        }
        return result
    }

    private fun resolveInclude(baseDir: String, includePath: String): String {
        val file = File(includePath)
        val resolved = if (file.isAbsolute) file else File(baseDir, includePath)
        return norm(resolved.path)
    }

    private fun norm(path: String): String =
        runCatching { File(path).canonicalPath }.getOrDefault(File(path).absolutePath)

    companion object {
        // #include "file.iss" | #include <file.iss> | #include file.iss
        private val INCLUDE = Regex(
            """#\s*include\s+(?:"([^"]+)"|<([^>]+)>|(\S+))""",
            RegexOption.IGNORE_CASE
        )

        /**
         * Extracts the raw include paths from ISPP `#include` directives in [text].
         */
        fun parseIncludes(text: String): List<String> =
            INCLUDE.findAll(text)
                .mapNotNull { m -> m.groupValues.drop(1).firstOrNull { it.isNotEmpty() } }
                .toList()
    }
}

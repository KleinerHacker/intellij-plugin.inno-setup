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

package org.pcsoft.intellij.plugin.inno_setup.build.run

import com.intellij.execution.RunManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSymbolProvider
import java.io.File

/**
 * Contributes the ISCC `/D…` symbols of the run configuration that compiles a given script, so the editor
 * decides `#ifdef` branches the same way the build will.
 *
 * Selection is deliberately narrow: only a configuration whose `scriptPath` *is* the analysed file
 * contributes. A configuration for a different script says nothing about this one, and guessing (for example
 * falling back to the selected configuration) would grey out branches based on an unrelated build.
 *
 * When several configurations target the same script — a common "Debug"/"Release" pair — only symbols they
 * **all** agree on are contributed. Anything they disagree about stays undefined here and therefore keeps its
 * condition undecidable, which is the honest answer: the outcome depends on which configuration is run.
 */
class IsRunConfigurationSymbolProvider : IsPreprocessorSymbolProvider {

    override fun symbols(project: Project, script: VirtualFile?): Map<String, String?> {
        val file = script ?: return emptyMap()

        val candidates = RunManager.getInstance(project).allConfigurationsList
            .filterIsInstance<IsRunConfiguration>()
            .filter { it.scriptPath.isNotBlank() && sameFile(it.scriptPath, file) }
            .map { IsCompilerDefines.parse(it.compilerDefines) }

        val first = candidates.firstOrNull() ?: return emptyMap()
        if (candidates.size == 1) return first

        return first.filter { (name, value) -> candidates.all { it.containsKey(name) && it[name] == value } }
    }

    /** Whether [path] denotes [file], compared canonically so `..` and separator style do not matter. */
    private fun sameFile(path: String, file: VirtualFile): Boolean =
        runCatching { File(path).canonicalFile == File(file.path).canonicalFile }.getOrDefault(false)
}

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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx

/**
 * Removes an `#include "path"` line whose target file is empty (the include does nothing).
 *
 * Stores only the literal path string — no PSI/VirtualFile references that could become stale
 * after the document is modified or the test fixture is torn down.
 */
class RemoveIncludeQuickFix : IntentionAction {

    private val path: String

    constructor(directive: IsPreprocessorDirective) {
        path = (directive as? IsPreprocessorDirectiveEx)?.getIncludePath().orEmpty()
    }

    /** Internal constructor for unit tests that avoids going through injection setup. */
    internal constructor(path: String) {
        this.path = path
    }

    override fun getText(): String = PluginBundle.message("intention.iss.include.remove.text")

    override fun getFamilyName(): String = PluginBundle.message("intention.iss.include.remove.text")

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean = path.isNotEmpty()

    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        if (path.isEmpty()) return

        // Resolve the ISS host file regardless of whether invoke() is called from the
        // host context or from an injected-language context.
        val issFile: IsScriptFile = when (file) {
            is IsScriptFile -> file
            else -> InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as? IsScriptFile ?: return
        }

        // When an editor is provided, use its document directly (essential for both the IDE and tests).
        val document = when {
            editor != null && editor.document.textLength > 0 -> editor.document
            else -> PsiDocumentManager.getInstance(project).getDocument(issFile) ?: return
        }

        val docText = document.text
        val lineCount = document.lineCount
        for (lineNum in 0 until lineCount) {
            val lineStart = document.getLineStartOffset(lineNum)
            val lineEnd = document.getLineEndOffset(lineNum)
            val lineText = docText.substring(lineStart, lineEnd)
            if (matchesInclude(lineText, path)) {
                // Include the trailing newline to avoid leaving a blank line.
                val deleteEnd = if (lineEnd < docText.length && docText[lineEnd] in listOf('\n', '\r')) {
                    if (docText[lineEnd] == '\r' && lineEnd + 1 < docText.length && docText[lineEnd + 1] == '\n')
                        lineEnd + 2 else lineEnd + 1
                } else lineEnd
                document.deleteString(lineStart, deleteEnd)
                return
            }
        }
    }

    override fun startInWriteAction(): Boolean = true
}

/** Whether [lineText] is an `#include` line referencing [path] (the quoted literal path). */
internal fun matchesInclude(lineText: String, path: String): Boolean {
    val trimmed = lineText.trim()
    if (!trimmed.startsWith("#include", ignoreCase = true)) return false
    return trimmed.contains("\"$path\"")
}

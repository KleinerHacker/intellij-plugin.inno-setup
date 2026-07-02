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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorHost
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx

/**
 * Removes an `#undef` directive that has no matching preceding `#define` (so it does nothing).
 *
 * Mirrors [RemoveUnusedDefineQuickFix]: only the undefined name is stored (no PSI/VirtualFile that
 * could become stale), and the matching `#undef <name>` line is deleted from the host document.
 */
class RemoveUselessUndefQuickFix : IntentionAction {

    private val undefName: String

    constructor(directive: IsPreprocessorDirective) {
        undefName = (directive as? IsPreprocessorDirectiveEx)?.name.orEmpty()
    }

    /** Test hook: construct directly from the name, bypassing injection setup (used from :plugin tests). */
    constructor(undefName: String) {
        this.undefName = undefName
    }

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String = "Remove #undef"

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getFamilyName(): String = "Remove #undef"

    /**
     * Checks whether this action can run in the current editor context.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean = undefName.isNotEmpty()

    /**
     * Executes this action against the current PSI file.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        if (undefName.isEmpty()) return

        val issFile: PsiFile = when (file) {
            is IsPreprocessorHost -> file
            else -> InjectedLanguageManager.getInstance(project).getTopLevelFile(file)
                .takeIf { it is IsPreprocessorHost } ?: return
        }

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
            if (matchesUndef(lineText, undefName)) {
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

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true

    private fun matchesUndef(lineText: String, name: String): Boolean {
        val trimmed = lineText.trim()
        if (!trimmed.startsWith("#undef", ignoreCase = true)) return false
        // Text after "#undef", skipping an optional scope keyword (public/protected/private).
        var rest = trimmed.substring(6).trimStart()
        for (scope in listOf("public", "protected", "private")) {
            if (rest.startsWith(scope, ignoreCase = true)) {
                val after = rest.getOrNull(scope.length)
                if (after != null && after.isWhitespace()) {
                    rest = rest.substring(scope.length).trimStart()
                    break
                }
            }
        }
        if (!rest.startsWith(name, ignoreCase = true)) return false
        val charAfterName = rest.getOrNull(name.length)
        return charAfterName == null || charAfterName.isWhitespace()
    }
}

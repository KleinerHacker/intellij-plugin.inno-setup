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
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx

/**
 * Implements an IntelliJ quick fix or intention for Inno Setup PSI.
 */
class RemoveUnusedDefineQuickFix : IntentionAction {

    // Store only the define name — no PSI/VirtualFile references that could become stale
    // after the document is modified or the test fixture is torn down.
    private val defineName: String

    constructor(directive: IsPreprocessorDirective) {
        defineName = (directive as? IsPreprocessorDirectiveEx)?.getDefineName().orEmpty()
    }

    /** Internal constructor for unit tests that avoids going through injection setup. */
    internal constructor(defineName: String) {
        this.defineName = defineName
    }

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String = "Remove unused #define"

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getFamilyName(): String = "Remove unused #define"

    /**
     * Checks whether this action can run in the current editor context.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean = defineName.isNotEmpty()

    /**
     * Executes this action against the current PSI file.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        if (defineName.isEmpty()) return

        // Resolve the ISS host file regardless of whether invoke() is called from the
        // host context or from an injected-language context.
        val issFile: IsScriptFile = when (file) {
            is IsScriptFile -> file
            else -> InjectedLanguageManager.getInstance(project).getTopLevelFile(file) as? IsScriptFile ?: return
        }

        // When an editor is provided, use its document directly. This is essential for both
        // the IDE (where the editor is the ISS host editor) and tests (where
        // PsiDocumentManager.getDocument() can return a partial injected document).
        val document = when {
            editor != null && editor.document.textLength > 0 -> editor.document
            else -> PsiDocumentManager.getInstance(project).getDocument(issFile) ?: return
        }

        // Search line-by-line in the document text (avoids PSI-coordinate issues with
        // injected languages where IsSectionPreprocessorLine.textRange can be in injected coordinates).
        val docText = document.text
        val lineCount = document.lineCount
        for (lineNum in 0 until lineCount) {
            val lineStart = document.getLineStartOffset(lineNum)
            val lineEnd = document.getLineEndOffset(lineNum)
            val lineText = docText.substring(lineStart, lineEnd)
            if (matchesDefine(lineText, defineName)) {
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

    private fun matchesDefine(lineText: String, name: String): Boolean {
        val trimmed = lineText.trim()
        if (!trimmed.startsWith("#define", ignoreCase = true)) return false
        val afterKeyword = trimmed.substring(7).trimStart()  // text after "#define"
        if (!afterKeyword.startsWith(name, ignoreCase = true)) return false
        val charAfterName = afterKeyword.getOrNull(name.length)
        return charAfterName == null || charAfterName.isWhitespace() || charAfterName == '('
    }
}

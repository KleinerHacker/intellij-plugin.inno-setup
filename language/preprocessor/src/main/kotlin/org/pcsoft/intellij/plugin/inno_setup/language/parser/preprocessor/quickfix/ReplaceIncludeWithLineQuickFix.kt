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
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorHost

/**
 * Replaces an `#include "path"` line whose target file holds exactly one line with that single line
 * (the inclusion is so trivial it can be written inline).
 *
 * Stores only stable strings (the literal path and the replacement line) — no PSI/VirtualFile
 * references that could become stale after the document is modified or the fixture is torn down.
 */
class ReplaceIncludeWithLineQuickFix(
    private val path: String,
    private val replacement: String,
) : IntentionAction {

    override fun getText(): String = PluginBundle.message("intention.iss.include.replace_line.text")

    override fun getFamilyName(): String = PluginBundle.message("intention.iss.include.replace_line.text")

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean = path.isNotEmpty()

    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        if (path.isEmpty()) return

        // Resolve the ISS host file regardless of whether invoke() is called from the
        // host context or from an injected-language context.
        val issFile: PsiFile = when (file) {
            is IsPreprocessorHost -> file
            else -> InjectedLanguageManager.getInstance(project).getTopLevelFile(file).takeIf { it is IsPreprocessorHost } ?: return
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
            if (matchesInclude(lineText, path)) {
                // Replace only the line content, keeping the existing line break intact.
                document.replaceString(lineStart, lineEnd, replacement)
                return
            }
        }
    }

    override fun startInWriteAction(): Boolean = true
}

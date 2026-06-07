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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.sections

class MoveCodeSectionLastQuickFix(file: IssFile) : IntentionAction {

    private val filePointer =
        SmartPointerManager.getInstance(file.project).createSmartPsiElementPointer(file)

    override fun getText(): String = "Move [Code] section to end of file"

    override fun getFamilyName(): String = "Move [Code] to last section"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean {
        val issFile = filePointer.element ?: return false
        val sections = issFile.sections()
        val codeIdx = sections.indexOfFirst { it.nameText().equals("Code", ignoreCase = true) }
        return codeIdx >= 0 && codeIdx < sections.size - 1
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val issFile = filePointer.element ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(issFile) ?: return
        PsiDocumentManager.getInstance(project).commitDocument(document)

        val codeSection = issFile.sections()
            .firstOrNull { it.nameText().equals("Code", ignoreCase = true) } ?: return

        val fullText = document.text
        val codeStart = codeSection.textRange.startOffset
        val codeEnd = codeSection.textRange.endOffset

        // Extract and normalize: the section's textRange may include trailing blank lines
        // absorbed as CRLF entries. trimEnd on surrounding parts and trim on the section
        // content ensure clean two-blank-line separation regardless of parser quirks.
        val beforeCode = fullText.substring(0, codeStart).trimEnd()
        val codeText = fullText.substring(codeStart, codeEnd).trim()
        val afterCode = fullText.substring(codeEnd).trimEnd()

        val newText = buildString {
            append(beforeCode)
            if (afterCode.isNotBlank()) {
                append("\n\n")
                append(afterCode.trim())
            }
            append("\n\n")
            append(codeText)
            append("\n")
        }
        document.setText(newText)
    }

    override fun startInWriteAction(): Boolean = true
}

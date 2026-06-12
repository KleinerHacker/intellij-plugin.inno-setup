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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionBlock

class RemoveEmptySectionQuickFix(section: IsSectionBlock) : IntentionAction {

    private val sectionPointer =
        SmartPointerManager.getInstance(section.project).createSmartPsiElementPointer(section)

    override fun getText(): String = "Remove empty section"

    override fun getFamilyName(): String = "Remove empty section"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean {
        val section = sectionPointer.element ?: return false
        return section.directiveEntryList.isEmpty() && section.parameterEntryList.isEmpty()
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val section = sectionPointer.element ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(section.containingFile) ?: return
        PsiDocumentManager.getInstance(project).commitDocument(document)

        val fullText = document.text
        val start = section.textRange.startOffset
        val end = section.textRange.endOffset

        val before = fullText.substring(0, start).trimEnd()
        val after = fullText.substring(end).trimStart('\n', '\r')

        val newText = if (before.isEmpty()) after else if (after.isEmpty()) "$before\n" else "$before\n\n$after"
        document.setText(newText)
    }

    override fun startInWriteAction(): Boolean = true
}

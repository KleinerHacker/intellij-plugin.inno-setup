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
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionDefSpec

class AddMissingDirectivesQuickFix(
    section: IsSectionBlock,
    private val missingNames: List<String>,
    private val specSection: IsSectionDefSpec
) : IntentionAction {

    private val sectionPointer =
        SmartPointerManager.getInstance(section.project).createSmartPsiElementPointer(section)

    override fun getText(): String {
        val properNames = missingNames.mapNotNull { name ->
            specSection.attributes.firstOrNull { it.name.equals(name, ignoreCase = true) }?.name
        }
        return "Add missing directive(s): " + properNames.joinToString(", ")
    }

    override fun getFamilyName(): String = "Add missing required directives"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean =
        sectionPointer.element != null

    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val section = sectionPointer.element ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(section.containingFile) ?: return

        val insertOffset = section.textRange.endOffset
        val text = buildString {
            for (name in missingNames) {
                val attr = specSection.attributes.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: continue
                append("${attr.name}=${IsSectionDefaultValueGenerator.defaultFor(attr)}\n")
            }
        }
        document.insertString(insertOffset, text)
    }

    override fun startInWriteAction(): Boolean = true
}

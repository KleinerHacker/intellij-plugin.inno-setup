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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.script.types.IsSectionDefSpec

/**
 * Implements an IntelliJ quick fix or intention for Inno Setup PSI.
 */
class AddMissingParametersQuickFix(
    entry: IsSectionParameterEntry,
    private val missingNames: List<String>,
    private val specSection: IsSectionDefSpec
) : IntentionAction {

    private val entryPointer =
        SmartPointerManager.getInstance(entry.project).createSmartPsiElementPointer(entry)

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String {
        val properNames = missingNames.mapNotNull { name ->
            specSection.attributes.firstOrNull { it.name.equals(name, ignoreCase = true) }?.name
        }
        return "Add missing parameter(s): " + properNames.joinToString(", ")
    }

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getFamilyName(): String = "Add missing required parameters"

    /**
     * Checks whether this action can run in the current editor context.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean =
        entryPointer.element != null

    /**
     * Executes this action against the current PSI file.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val entry = entryPointer.element ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(entry.containingFile) ?: return

        val insertOffset = entry.node.lastChildNode
            ?.takeIf { it.elementType == IsSectionTypes.CRLF }
            ?.startOffset ?: entry.textRange.endOffset

        val text = buildString {
            for (name in missingNames) {
                val attr = specSection.attributes.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: continue
                append("; ${attr.name}: ${IsSectionDefaultValueGenerator.defaultFor(attr)}")
            }
        }
        document.insertString(insertOffset, text)
    }

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true
}

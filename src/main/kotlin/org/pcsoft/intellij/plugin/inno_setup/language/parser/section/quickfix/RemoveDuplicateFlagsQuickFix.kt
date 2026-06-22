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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTypes

/**
 * Removes every duplicate occurrence of a flag from a flags [IsSectionParamValue], keeping only the first.
 * E.g. `Flags: ignoreversion recursesubdirs ignoreversion` → `Flags: ignoreversion recursesubdirs`.
 */
class RemoveDuplicateFlagsQuickFix(value: IsSectionParamValue, private val flagText: String) : IntentionAction {

    private val valuePointer =
        SmartPointerManager.getInstance(value.project).createSmartPsiElementPointer(value)

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String = "Remove duplicate flag '$flagText'"

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getFamilyName(): String = "Remove duplicate flag"

    /**
     * Checks whether this action can run in the current editor context.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean =
        duplicateNodes().isNotEmpty()

    /**
     * Executes this action against the current PSI file.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val value = valuePointer.element ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(value.containingFile) ?: return

        // Delete from the back so earlier offsets stay valid.
        duplicateNodes().sortedByDescending { it.startOffset }.forEach { node ->
            var start = node.startOffset
            var end = node.startOffset + node.textLength
            // Also remove one adjacent whitespace gap so no double/leading/trailing separator remains.
            val next = node.treeNext
            val prev = node.treePrev
            if (next != null && next.elementType == TokenType.WHITE_SPACE) {
                end = next.startOffset + next.textLength
            } else if (prev != null && prev.elementType == TokenType.WHITE_SPACE) {
                start = prev.startOffset
            }
            document.deleteString(start, end)
        }
    }

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true

    /** All but the first occurrence of [flagText] within the value. */
    private fun duplicateNodes(): List<ASTNode> {
        val value = valuePointer.element ?: return emptyList()
        return value.node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
            .filter { it.text.equals(flagText, ignoreCase = true) }
            .drop(1)
    }
}

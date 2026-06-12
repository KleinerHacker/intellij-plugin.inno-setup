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
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

/**
 * Removes a single redundant flag identifier (and one adjacent whitespace gap) from a flags
 * [IsSectionParamValue], e.g. removing `nocompression` from `Flags: external nocompression`.
 */
class RemoveRedundantFlagQuickFix(value: IsSectionParamValue, private val flagText: String) : IntentionAction {

    private val valuePointer =
        SmartPointerManager.getInstance(value.project).createSmartPsiElementPointer(value)

    override fun getText(): String = "Remove redundant flag '$flagText'"

    override fun getFamilyName(): String = "Remove redundant flag"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean =
        findFlagNode() != null

    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val value = valuePointer.element ?: return
        val node = findFlagNode() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(value.containingFile) ?: return

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

    override fun startInWriteAction(): Boolean = true

    private fun findFlagNode(): ASTNode? {
        val value = valuePointer.element ?: return null
        return value.node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
            .firstOrNull { it.text.equals(flagText, ignoreCase = true) }
    }
}

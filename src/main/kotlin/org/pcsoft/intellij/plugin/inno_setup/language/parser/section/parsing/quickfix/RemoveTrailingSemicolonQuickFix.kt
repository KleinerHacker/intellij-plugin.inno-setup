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
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

/**
 * Implements an IntelliJ quick fix or intention for Inno Setup PSI.
 */
class RemoveTrailingSemicolonQuickFix(entry: IsSectionParameterEntry) : IntentionAction {

    private val entryPointer =
        SmartPointerManager.getInstance(entry.project).createSmartPsiElementPointer(entry)

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String = "Remove trailing semicolon"

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getFamilyName(): String = "Remove trailing semicolon"

    /**
     * Checks whether this action can run in the current editor context.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean =
        findSemicolon() != null

    /**
     * Executes this action against the current PSI file.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val entry = entryPointer.element ?: return
        val semi = findSemicolon() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(entry.containingFile) ?: return
        document.deleteString(semi.startOffset, semi.startOffset + semi.textLength)
    }

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true

    private fun findSemicolon(): ASTNode? {
        val entry = entryPointer.element ?: return null
        var node = entry.node.lastChildNode
        if (node?.elementType == IsSectionTypes.CRLF) node = node.treePrev
        return node?.takeIf { it.elementType == IsSectionTypes.SEMICOLON }
    }
}

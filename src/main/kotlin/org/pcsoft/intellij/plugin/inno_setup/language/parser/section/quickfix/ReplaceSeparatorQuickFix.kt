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
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.tree.IElementType

/**
 * Replaces the wrong key/value separator of a single entry with the one its section expects: ':' →
 * '=' inside a directive section, or '=' → ':' inside a parameter section. [holderElement] is the
 * pair/entry that owns the separator token; [tokenType] is the wrong separator's token type and
 * [replacement] the correct character.
 */
class ReplaceSeparatorQuickFix(
    holderElement: PsiElement,
    private val tokenType: IElementType,
    private val wrong: String,
    private val replacement: String
) : IntentionAction {

    private val elementPointer =
        SmartPointerManager.getInstance(holderElement.project).createSmartPsiElementPointer(holderElement)

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String = "Replace '$wrong' with '$replacement'"

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getFamilyName(): String = "Fix key/value separator"

    /**
     * Checks whether this action can run in the current editor context.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean =
        findSeparator() != null

    /**
     * Executes this action against the current PSI file.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val element = elementPointer.element ?: return
        val separator = findSeparator() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(element.containingFile) ?: return
        document.replaceString(separator.startOffset, separator.startOffset + separator.textLength, replacement)
    }

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true

    private fun findSeparator(): ASTNode? = elementPointer.element?.node?.findChildByType(tokenType)
}

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
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTypes

/**
 * Appends one or more required flag identifiers to a flags [IsSectionParamValue], e.g. adding
 * `external ignoreversion` to `Flags: extractarchive`. Only the flags still missing from the
 * value are inserted, separated by single spaces, after the existing flag list.
 */
class AddMissingFlagsQuickFix(value: IsSectionParamValue, private val requiredFlags: List<String>) : IntentionAction {

    private val valuePointer =
        SmartPointerManager.getInstance(value.project).createSmartPsiElementPointer(value)

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String = "Add required flag(s) '${missingFlags().joinToString(", ")}'"

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getFamilyName(): String = "Add required flags"

    /**
     * Checks whether this action can run in the current editor context.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean =
        missingFlags().isNotEmpty()

    /**
     * Executes this action against the current PSI file.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val value = valuePointer.element ?: return
        val missing = missingFlags()
        if (missing.isEmpty()) return
        val document = PsiDocumentManager.getInstance(project).getDocument(value.containingFile) ?: return

        // Insert after the last existing flag identifier so the current list stays intact.
        val lastFlag = value.node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER)).lastOrNull()
        val offset = lastFlag?.let { it.startOffset + it.textLength } ?: value.textRange.endOffset
        document.insertString(offset, " " + missing.joinToString(" "))
    }

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true

    private fun missingFlags(): List<String> {
        val value = valuePointer.element ?: return emptyList()
        val present = value.node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER))
            .map { it.text.lowercase() }
            .toSet()
        return requiredFlags.filter { it.lowercase() !in present }
    }
}

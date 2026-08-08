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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.isInCodeSection
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.nextParam
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamPair
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParameterEntry

/**
 * Intention action that swaps two adjacent parameters in an Inno Setup section entry.
 *
 * The action is offered when the caret is positioned after a parameter separator, allowing users
 * to move the parameter before the separator behind the parameter that follows it.
 */
class IsParameterFlipIntentionAction : IntentionAction {
    /**
     * Returns the label shown in the intention popup.
     */
    override fun getText(): @IntentionName String = "Flip parameters"

    /**
     * Checks whether the caret is placed at a parameter separator inside a section parameter
     * entry.
     *
     * The editor and file are nullable because IntelliJ may ask for availability outside of an
     * editor-backed context; in those cases the intention is not available.
     *
     * Inside \[Code] the intention is never offered: a `;` there terminates a Pascal statement rather than
     * separating ISS parameters, so flipping around it would corrupt the code.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val offset = editor?.caretModel?.offset ?: return false
        val sign = file?.text?.elementAt(0.coerceAtLeast(offset - 1)) ?: return false
        val elementAt = file.findElementAt(offset) ?: return false
        if (elementAt.isInCodeSection) return false

        return PsiTreeUtil.getParentOfType(elementAt, IsSectionParameterEntry::class.java) != null && sign == ';'
    }

    /**
     * Swaps the parameter before the current separator with the next parameter in the same section
     * entry.
     *
     * If the caret is no longer on a valid pair of adjacent parameters when the action runs, the
     * method exits without changing the PSI tree.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val offset = editor?.caretModel?.offset ?: return
        val elementAt = file?.findElementAt(0.coerceAtLeast(offset - 2)) ?: return

        val parameter1 = PsiTreeUtil.getParentOfType(elementAt, IsSectionParamPair::class.java) ?: return
        val parameter2 = parameter1.nextParam ?: return

        val tmp = parameter1.copy()
        parameter1.replace(parameter2)
        parameter2.replace(tmp)
    }

    /**
     * Indicates that the PSI replacement is performed inside IntelliJ's write action.
     */
    override fun startInWriteAction(): Boolean = true

    /**
     * Returns the common family name used to group Inno Setup intentions.
     */
    override fun getFamilyName(): @IntentionFamilyName String = "Inno Setup"
}

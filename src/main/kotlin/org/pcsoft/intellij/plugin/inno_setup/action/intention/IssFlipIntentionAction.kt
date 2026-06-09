/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.action.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nextParam
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParameterEntry

class IssFlipIntentionAction : IntentionAction {
    override fun getText(): @IntentionName String = "Flip parameters"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val offset = editor?.caretModel?.offset ?: return false
        val sign = file?.text?.elementAt(0.coerceAtLeast(offset - 1)) ?: return false
        val elementAt = file?.findElementAt(offset) ?: return false

        return PsiTreeUtil.getParentOfType(elementAt, IsiParameterEntry::class.java) != null && sign == ';'
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val offset = editor?.caretModel?.offset ?: return
        val elementAt = file?.findElementAt(0.coerceAtLeast(offset - 2)) ?: return

        val parameter1 = PsiTreeUtil.getParentOfType(elementAt, IsiParamPair::class.java) ?: return
        val parameter2 = parameter1.nextParam ?: return

        val tmp = parameter1.copy()
        parameter1.replace(parameter2)
        parameter2.replace(tmp)
    }

    override fun startInWriteAction(): Boolean = true

    override fun getFamilyName(): @IntentionFamilyName String = "Inno Setup"
}
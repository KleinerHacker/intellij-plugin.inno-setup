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
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamValue

/**
 * Turns the doubled closing brace of a `{{…}}` into a single one.
 *
 * Inno Setup escapes an opening `{` as `{{`, but a closing `}` is never escaped: `{{GUID}}` becomes the text
 * `{GUID}}`, with a stray `}` at the end. The fix removes that extra `}` so `{{GUID}` remains, which the
 * compiler turns into `{GUID}`.
 *
 * @param value the value that holds the `{{…}}`
 * @param extraBraceOffset offset of the extra (second) closing brace relative to the start of [value]
 */
class RemoveExtraClosingBraceQuickFix(value: IsSectionParamValue, private val extraBraceOffset: Int) : IntentionAction {

    private val valuePointer =
        SmartPointerManager.getInstance(value.project).createSmartPsiElementPointer(value)

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String = "Use a single '}' (remove the extra closing brace)"

    /**
     * Returns the user-visible family name used by IntelliJ.
     */
    override fun getFamilyName(): String = "Remove extra closing brace"

    /**
     * Checks whether this action can run in the current editor context: the extra brace must still follow
     * another closing brace.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean = extraBraceRange() != null

    /**
     * Executes this action against the current PSI file by deleting the extra closing brace.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val range = extraBraceRange() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return
        document.deleteString(range.first, range.second)
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true

    /** The document range of the extra brace, or `null` when the text no longer has the `}}` shape. */
    private fun extraBraceRange(): Pair<Int, Int>? {
        val value = valuePointer.element ?: return null
        val text = value.text
        if (extraBraceOffset < 1 || extraBraceOffset >= text.length) return null
        if (text[extraBraceOffset] != '}' || text[extraBraceOffset - 1] != '}') return null
        val start = value.textRange.startOffset + extraBraceOffset
        return start to start + 1
    }
}

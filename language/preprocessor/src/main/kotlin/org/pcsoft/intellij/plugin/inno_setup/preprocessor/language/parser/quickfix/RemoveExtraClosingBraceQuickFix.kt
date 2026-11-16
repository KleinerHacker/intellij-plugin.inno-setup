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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager

/**
 * Turns the doubled closing brace of a `{{…}}` into a single one.
 *
 * Inno Setup escapes an opening `{` as `{{`, but a closing `}` is never escaped: `{{GUID}}` becomes the text
 * `{GUID}}`, with a stray `}` at the end. The fix removes that extra `}` so `{{GUID}` remains, which the
 * compiler turns into `{GUID}`.
 *
 * It works for a script value and for a preprocessor directive alike (the directive is an injected fragment;
 * its document maps the edit back to the host file).
 *
 * @param element the element that holds the `{{…}}` (a script value or a preprocessor directive)
 * @param extraBraceOffset offset of the extra (second) closing brace relative to the start of [element]
 */
class RemoveExtraClosingBraceQuickFix(element: PsiElement, private val extraBraceOffset: Int) : IntentionAction {

    private val elementPointer =
        SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(element)

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
        val element = elementPointer.element ?: return
        val range = extraBraceRange() ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(element.containingFile) ?: return
        document.deleteString(range.first, range.second)
        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true

    /** The range of the extra brace in the document of its file, or `null` when the text no longer has the `}}` shape. */
    private fun extraBraceRange(): Pair<Int, Int>? {
        val element = elementPointer.element ?: return null
        val text = element.text
        if (extraBraceOffset < 1 || extraBraceOffset >= text.length) return null
        if (text[extraBraceOffset] != '}' || text[extraBraceOffset - 1] != '}') return null
        val start = element.textRange.startOffset + extraBraceOffset
        return start to start + 1
    }
}

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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.editor

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nextSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.prevSection

class IsiSectionMover : StatementUpDownMover() {
    override fun checkAvailable(editor: Editor, file: PsiFile, info: MoveInfo, down: Boolean): Boolean {
        val offset = editor.caretModel.offset
        val elementAt = file.findElementAt(offset) ?: return false
        val section = PsiTreeUtil.getTopmostParentOfType(elementAt, IsiSection::class.java) ?: return false

        if ((!down && section.prevSibling == null) || (down && section.nextSibling == null))
            return false

        info.toMove = LineRange(section)
        (if (down) section.nextSection else section.prevSection)?.let { info.toMove2 = LineRange(it) }

        return true
    }
}
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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nextSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.prevSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionBlock

/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
class IsSectionMover : StatementUpDownMover() {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun checkAvailable(editor: Editor, file: PsiFile, info: MoveInfo, down: Boolean): Boolean {
        val offset = editor.caretModel.offset
        val elementAt = file.findElementAt(offset) ?: file.findElementAt(offset - 1) ?: return false
        val section = PsiTreeUtil.getParentOfType(elementAt, IsSectionBlock::class.java) ?: return false

        // Only move whole sections when the caret is on the section header line itself (e.g. on
        // "\[Setup]"). Anywhere else inside the section the generic line mover should keep working,
        // so parameter lines can still be shuffled one at a time.
        val document = editor.document
        val headerLine = document.getLineNumber(section.header.textRange.startOffset)
        if (document.getLineNumber(offset) != headerLine)
            return false

        // Don't reindent — ISS sections are flush-left and have no formatter-driven indentation.
        info.indentTarget = false
        info.indentSource = false

        val target = if (down) section.nextSection else section.prevSection
        if (target == null) {
            // Caret sits in a section, but there is no neighbouring section in the requested
            // direction. Claim the move and prohibit it so the generic line mover does not kick
            // in and shuffle a single line out of the section.
            info.toMove = section.lineRange(editor)
            info.prohibitMove()
            return true
        }

        info.toMove = section.lineRange(editor)
        info.toMove2 = target.lineRange(editor)

        return true
    }

    // A section's PSI range absorbs the trailing CR/LF of the blank lines that follow it
    // (`entry ::= … | CRLF`), so its end offset usually points at the *start* of the next
    // section's line. Feeding that straight into LineRange(PsiElement) would count one line too
    // many and overlap the neighbour, which makes the platform reject the move ("Cannot perform
    // move"). Resolve the last line from endOffset - 1 instead.
    private fun IsSectionBlock.lineRange(editor: Editor): LineRange {
        val document = editor.document
        val range = textRange
        val startLine = document.getLineNumber(range.startOffset)
        val lastOffset = (range.endOffset - 1).coerceAtLeast(range.startOffset)
        val endLine = document.getLineNumber(lastOffset)
        return LineRange(startLine, endLine + 1)
    }
}

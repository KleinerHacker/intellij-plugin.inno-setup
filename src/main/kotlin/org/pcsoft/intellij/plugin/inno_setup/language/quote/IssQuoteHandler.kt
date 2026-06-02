package org.pcsoft.intellij.plugin.inno_setup.language.quote

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssTypes

class IssQuoteHandler : TypedHandlerDelegate() {

    override fun beforeCharTyped(
        c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType
    ): Result {
        if (file !is IssFile || c != '"') return Result.CONTINUE

        val offset = editor.caretModel.offset
        val text   = editor.document.charsSequence

        // Skip over an existing closing quote instead of inserting a new one
        if (offset < text.length && text[offset] == '"') {
            val iterator = (editor as EditorEx).highlighter.createIterator(offset)
            if (!iterator.atEnd()
                && iterator.tokenType == IssTypes.STRING
                && offset == iterator.end - 1) {
                editor.caretModel.moveToOffset(offset + 1)
                return Result.STOP
            }
        }

        // Don't auto-close when the cursor is already inside a string literal
        if (offset > 0) {
            val iterator = (editor as EditorEx).highlighter.createIterator(offset - 1)
            if (!iterator.atEnd()
                && iterator.tokenType == IssTypes.STRING
                && offset > iterator.start
                && offset < iterator.end) {
                return Result.CONTINUE
            }
        }

        // Auto-insert the matching closing quote and place the cursor between them
        editor.document.insertString(offset, "\"\"")
        editor.caretModel.moveToOffset(offset + 1)
        return Result.STOP
    }
}

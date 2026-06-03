package org.pcsoft.intellij.plugin.inno_setup.language.editor

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

class IssQuoteHandler : TypedHandlerDelegate() {

    override fun beforeCharTyped(
        c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType
    ): Result {
        if (file !is IssFile || c != '"') return Result.CONTINUE

        val offset  = editor.caretModel.offset
        val text    = editor.document.charsSequence
        val editorEx = editor as EditorEx

        // Skip over an existing closing quote instead of inserting a new one.
        // A QUOTE token is closing when the preceding token is QUOTE (empty string),
        // STRING_PART (content), or RBRACE (end of embedded constant).
        if (offset < text.length && text[offset] == '"') {
            val iter = editorEx.highlighter.createIterator(offset)
            if (!iter.atEnd() && iter.tokenType == IssTypes.QUOTE && offset > 0) {
                val prev = editorEx.highlighter.createIterator(offset - 1)
                if (!prev.atEnd()) {
                    val pt = prev.tokenType
                    if (pt == IssTypes.QUOTE || pt == IssTypes.STRING_PART || pt == IssTypes.RBRACE) {
                        editor.caretModel.moveToOffset(offset + 1)
                        return Result.STOP
                    }
                }
            }
        }

        // Don't auto-close when the cursor is already inside a string literal.
        if (offset > 0) {
            val iter = editorEx.highlighter.createIterator(offset - 1)
            if (!iter.atEnd()) {
                when (iter.tokenType) {
                    IssTypes.STRING_PART, IssTypes.RBRACE -> return Result.CONTINUE
                    IssTypes.QUOTE -> {
                        // Only suppress auto-close if this is an *opening* QUOTE, i.e. the token
                        // before it is not another string-content token (which would make it closing).
                        val pos = iter.start
                        if (pos > 0) {
                            val pp = editorEx.highlighter.createIterator(pos - 1)
                            if (!pp.atEnd()) {
                                val ppt = pp.tokenType
                                if (ppt != IssTypes.QUOTE && ppt != IssTypes.STRING_PART && ppt != IssTypes.RBRACE) {
                                    return Result.CONTINUE
                                }
                            }
                        } else {
                            return Result.CONTINUE
                        }
                    }
                }
            }
        }

        // Auto-insert the matching closing quote and place the cursor between them
        editor.document.insertString(offset, "\"\"")
        editor.caretModel.moveToOffset(offset + 1)
        return Result.STOP
    }
}

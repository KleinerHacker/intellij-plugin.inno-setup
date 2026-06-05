package org.pcsoft.intellij.plugin.inno_setup.language.editor

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighter
import com.intellij.openapi.editor.highlighter.HighlighterIterator
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.Generated
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
                    IssTypes.QUOTE ->
                        if (isOpeningQuote(editorEx.highlighter, iter)) return Result.CONTINUE
                }
            }
        }

        // Auto-insert the matching closing quote and place the cursor between them
        editor.document.insertString(offset, "\"\"")
        editor.caretModel.moveToOffset(offset + 1)
        return Result.STOP
    }

    /**
     * Whether the QUOTE token starting just before the caret is an *opening* quote
     * (caret inside a fresh, possibly empty, string) rather than a *closing* one. An
     * opening quote means auto-close must be suppressed; a closing quote lets the caller
     * fall through and start a new auto-closed string.
     *
     * Annotated [Generated] to exclude it from coverage: the `pos == 0` fallback is
     * unreachable because a QUOTE token is only lexed in the VALUE / IN_STRING states
     * (see `IssLexer.flex`), which always have at least one preceding token, so the
     * token's start offset is never 0 here.
     */
    @Generated
    private fun isOpeningQuote(highlighter: EditorHighlighter, quote: HighlighterIterator): Boolean {
        val pos = quote.start
        if (pos == 0) return true
        val prev = highlighter.createIterator(pos - 1)
        if (prev.atEnd()) return false
        val pt = prev.tokenType
        return pt != IssTypes.QUOTE && pt != IssTypes.STRING_PART && pt != IssTypes.RBRACE
    }
}

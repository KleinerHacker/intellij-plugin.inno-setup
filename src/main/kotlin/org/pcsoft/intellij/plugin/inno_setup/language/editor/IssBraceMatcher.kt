package org.pcsoft.intellij.plugin.inno_setup.language.editor

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

class IssBraceMatcher : PairedBraceMatcher {

    override fun getPairs(): Array<BracePair> = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset

    companion object {
        val PAIRS: Array<BracePair> = arrayOf(
            BracePair(IssTypes.LBRACKET, IssTypes.RBRACKET, true),
            BracePair(IssTypes.LBRACE,   IssTypes.RBRACE,   false),
            BracePair(IssTypes.LPAREN,   IssTypes.RPAREN,   false),
        )
    }
}

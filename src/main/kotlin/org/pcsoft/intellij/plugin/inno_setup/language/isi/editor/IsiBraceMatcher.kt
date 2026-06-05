package org.pcsoft.intellij.plugin.inno_setup.language.isi.editor

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

class IsiBraceMatcher : PairedBraceMatcher {

    override fun getPairs(): Array<BracePair> = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset

    companion object {
        val PAIRS: Array<BracePair> = arrayOf(
            BracePair(IsiTypes.LBRACKET, IsiTypes.RBRACKET, true),
            BracePair(IsiTypes.LBRACE, IsiTypes.RBRACE, false),
            BracePair(IsiTypes.LPAREN, IsiTypes.RPAREN, false),
        )
    }
}

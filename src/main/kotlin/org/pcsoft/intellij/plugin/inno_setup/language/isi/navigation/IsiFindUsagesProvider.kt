package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.IsiLexerAdapter
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes

class IsiFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner = DefaultWordsScanner(
        IsiLexerAdapter(),
        TokenSet.create(IsiTypes.IDENTIFIER),
        TokenSet.create(IsiTypes.COMMENT),
        TokenSet.create(IsiTypes.STRING_PART),
    )

    override fun canFindUsagesFor(element: PsiElement): Boolean =
        element is IsiParamPairEx && element.isNameDeclaration()

    override fun getHelpId(element: PsiElement): String? = null

    override fun getType(element: PsiElement): String = when {
        element is IsiParamPairEx && element.isNameDeclaration() -> "ISS named item"
        else -> ""
    }

    override fun getDescriptiveName(element: PsiElement): String =
        (element as? PsiNamedElement)?.name ?: element.text

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        getDescriptiveName(element)
}

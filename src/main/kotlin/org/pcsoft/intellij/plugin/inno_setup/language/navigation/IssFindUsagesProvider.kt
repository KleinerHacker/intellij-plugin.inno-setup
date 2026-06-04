package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.IssLexerAdapter
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

class IssFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner = DefaultWordsScanner(
        IssLexerAdapter(),
        TokenSet.create(IssTypes.IDENTIFIER),
        TokenSet.create(IssTypes.COMMENT),
        TokenSet.create(IssTypes.STRING_PART),
    )

    override fun canFindUsagesFor(element: PsiElement): Boolean =
        element is IssParamPairEx && element.isNameDeclaration()

    override fun getHelpId(element: PsiElement): String? = null

    override fun getType(element: PsiElement): String = when {
        element is IssParamPairEx && element.isNameDeclaration() -> "ISS named item"
        else -> ""
    }

    override fun getDescriptiveName(element: PsiElement): String =
        (element as? PsiNamedElement)?.name ?: element.text

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        getDescriptiveName(element)
}

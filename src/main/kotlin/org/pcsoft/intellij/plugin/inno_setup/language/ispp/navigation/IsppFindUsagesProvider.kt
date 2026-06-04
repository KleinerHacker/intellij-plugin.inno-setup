package org.pcsoft.intellij.plugin.inno_setup.language.ispp.navigation

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.IsppLexerAdapter
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes

class IsppFindUsagesProvider : FindUsagesProvider {

    override fun getWordsScanner(): WordsScanner = DefaultWordsScanner(
        IsppLexerAdapter(),
        TokenSet.create(IsppTypes.IDENTIFIER),
        TokenSet.EMPTY,
        TokenSet.create(IsppTypes.STRING_PART),
    )

    override fun canFindUsagesFor(element: PsiElement): Boolean =
        element is IsppDirectiveEx && element.isDefine()

    override fun getHelpId(element: PsiElement): String? = null

    override fun getType(element: PsiElement): String = "ISPP define"

    override fun getDescriptiveName(element: PsiElement): String =
        (element as? PsiNamedElement)?.name ?: element.text

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        getDescriptiveName(element)
}

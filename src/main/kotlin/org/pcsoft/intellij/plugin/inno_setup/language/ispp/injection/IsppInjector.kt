package org.pcsoft.intellij.plugin.inno_setup.language.ispp.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssIsppLine
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

class IsppInjector : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is IssIsppLine) return
        val lineNode = context.node.findChildByType(IssTypes.PREPROCESSOR_LINE) ?: return
        val startInHost = lineNode.startOffset - context.textRange.startOffset
        registrar.startInjecting(IsppLanguage)
            .addPlace(null, null, context as PsiLanguageInjectionHost,
                TextRange(startInHost, startInHost + lineNode.textLength))
            .doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(IssIsppLine::class.java)
}

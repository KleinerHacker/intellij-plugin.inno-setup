package org.pcsoft.intellij.plugin.inno_setup.language.ispp.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiIsppLine
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppLanguage

class IsppInjector : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is IsiIsppLine) return
        val lineNode = context.node.findChildByType(IsiTypes.PREPROCESSOR_LINE) ?: return
        val startInHost = lineNode.startOffset - context.textRange.startOffset
        registrar.startInjecting(IsppLanguage)
            .addPlace(
                null, null, context as PsiLanguageInjectionHost,
                TextRange(startInHost, startInHost + lineNode.textLength)
            )
            .doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(IsiIsppLine::class.java)
}

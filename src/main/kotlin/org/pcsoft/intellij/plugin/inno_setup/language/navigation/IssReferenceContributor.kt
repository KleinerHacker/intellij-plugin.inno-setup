package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.identifiers
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

class IssReferenceContributor : PsiReferenceContributor() {
    companion object {
        private val REF_KEY_TO_SECTION = mapOf(
            "tasks" to "Tasks",
            "components" to "Components",
            "types" to "Types",
            "languages" to "Languages",
        )
    }

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(IssParamValue::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement, context: ProcessingContext
                ): Array<PsiReference> {
                    val pair = element.containingParamPair() ?: return emptyArray()
                    val targetSection = REF_KEY_TO_SECTION[pair.keyText().lowercase()]
                        ?: return emptyArray()
                    return (element as IssParamValue).identifiers()
                        .map { IssReference(it, targetSection) }
                        .toTypedArray()
                }
            }
        )

        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement().withElementType(IssTypes.IDENTIFIER)
                .withParent(IssConstantBody::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement, context: ProcessingContext
                ): Array<PsiReference> {
                    val body = element.parent as? IssConstantBody ?: return emptyArray()
                    if (!body.text.startsWith("#")) return emptyArray()
                    val name = element.text
                    if (name.isEmpty()) return emptyArray()
                    return arrayOf(IssIsppConstantReference(element, name))
                }
            }
        )
    }
}

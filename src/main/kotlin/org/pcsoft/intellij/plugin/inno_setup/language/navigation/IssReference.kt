package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import org.pcsoft.intellij.plugin.inno_setup.language.findSections
import org.pcsoft.intellij.plugin.inno_setup.language.issFile
import org.pcsoft.intellij.plugin.inno_setup.language.nameDeclarations
import org.pcsoft.intellij.plugin.inno_setup.language.valueUnquoted

class IssReference(element: PsiElement, private val targetSection: String)
    : PsiReferenceBase<PsiElement>(element, TextRange(0, element.textLength)) {

    override fun resolve(): PsiElement? {
        val file = element.issFile() ?: return null
        val name = element.text.removeSurrounding("\"")
        return file.findSections(targetSection)
            .flatMap { it.nameDeclarations() }
            .firstOrNull { it.valueUnquoted().equals(name, ignoreCase = true) }
            ?.paramValue
    }

    override fun getVariants(): Array<Any> {
        val file = element.issFile() ?: return emptyArray()
        return file.findSections(targetSection)
            .flatMap { it.nameDeclarations() }
            .mapNotNull { it.valueUnquoted().ifEmpty { null } }
            .map { LookupElementBuilder.create(it) }
            .toTypedArray()
    }
}

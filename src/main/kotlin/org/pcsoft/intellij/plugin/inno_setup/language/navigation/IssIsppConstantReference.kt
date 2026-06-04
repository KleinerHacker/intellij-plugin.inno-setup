package org.pcsoft.intellij.plugin.inno_setup.language.navigation

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes

// Anchor is IssConstantBody (not the IDENTIFIER leaf) so that getReferences() on the
// mixin element is the source of truth. TextRange skips the leading '#' character.
class IssIsppConstantReference(constantBody: IssConstantBody, private val name: String)
    : PsiReferenceBase<IssConstantBody>(constantBody, TextRange(1, 1 + name.length), true) {

    override fun resolve(): PsiElement? {
        val file = element.containingFile as? IssFile ?: return null
        return PsiTreeUtil.getChildrenOfTypeAsList(file, IssPreprocessorDirective::class.java)
            .firstOrNull { d ->
                (d as? IssPreprocessorDirectiveEx)?.isDefine() == true &&
                    d.getDefineName() == name
            }
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true
        val nameId = (resolved as? PsiNameIdentifierOwner)?.nameIdentifier
        return nameId != null && mgr.areElementsEquivalent(nameId, element)
    }

    override fun getVariants(): Array<Any> = emptyArray()

    override fun handleElementRename(newElementName: String): PsiElement {
        val oldId = element.node.findChildByType(IssTypes.IDENTIFIER)?.psi ?: return element
        // Use a #define dummy so the parser starts in YYINITIAL and produces an IDENTIFIER node.
        // A bare {#name} file would not parse in YYINITIAL context and would return null.
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("dummy.iss", IssFileType.INSTANCE, "#define $newElementName\n")
        val newId = PsiTreeUtil.findChildOfType(dummy, IssPreprocessorDirective::class.java)
            ?.paramValue?.node?.findChildByType(IssTypes.IDENTIFIER)?.psi ?: return element
        oldId.replace(newId)
        return element
    }
}

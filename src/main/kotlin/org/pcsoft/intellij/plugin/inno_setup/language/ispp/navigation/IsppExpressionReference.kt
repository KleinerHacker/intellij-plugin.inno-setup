package org.pcsoft.intellij.plugin.inno_setup.language.ispp.navigation

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReferenceBase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.isppDirectivesWithHostOffset

/**
 * A free-text identifier inside a `#define` expression that refers to another `#define`.
 *
 * Resolution is **declaration-order aware**: only `#define`s declared on a line *before* the line
 * holding this reference are considered (forward references do not resolve). The reference is `soft`
 * so unresolved identifiers (e.g. ISPP built-in functions) do not produce an error.
 *
 * Anchored on the [IsppDirective]; [rangeInElement] points at the identifier within the directive.
 */
class IsppExpressionReference(
    directive: IsppDirective,
    rangeStartInDirective: Int,
    private val name: String,
) : PsiReferenceBase<IsppDirective>(
    directive,
    TextRange(rangeStartInDirective, rangeStartInDirective + name.length),
    /* soft = */ true,
) {

    override fun resolve(): PsiElement? {
        val injMgr = InjectedLanguageManager.getInstance(element.project)
        val issFile = injMgr.getTopLevelFile(element.containingFile) as? IssFile ?: return null
        val hostLine = injMgr.getInjectionHost(element) ?: return null
        val currentOffset = hostLine.textRange.startOffset

        return issFile.isppDirectivesWithHostOffset()
            .filter { (d, offset) ->
                offset < currentOffset &&
                        (d as? IsppDirectiveEx)?.isDefine() == true &&
                        (d as? IsppDirectiveEx)?.getDefineName() == name
            }
            .maxByOrNull { it.second }   // nearest preceding declaration
            ?.first
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
        val injMgr = InjectedLanguageManager.getInstance(element.project)
        val range = rangeInElement.shiftRight(element.textRange.startOffset) // injected coords
        val hostRange = injMgr.injectedToHost(element, range)
        val hostFile = injMgr.getTopLevelFile(element.containingFile)
        val docManager = PsiDocumentManager.getInstance(element.project)
        val doc = docManager.getDocument(hostFile) ?: return element
        docManager.doPostponedOperationsAndUnblockDocument(doc)
        doc.replaceString(hostRange.startOffset, hostRange.endOffset, newElementName)
        docManager.commitDocument(doc)
        return element
    }
}

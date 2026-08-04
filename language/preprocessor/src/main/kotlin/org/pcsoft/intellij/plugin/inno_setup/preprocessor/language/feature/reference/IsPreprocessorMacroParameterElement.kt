/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.reference

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.FakePsiElement
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import javax.swing.Icon

/**
 * The declaration of a function-like macro parameter (`#define M(int A, B = 10) …`) as a navigable, renameable
 * element.
 *
 * ISPP declares parameters inside the free-form value text of the `#define`, so there is no PSI node of its
 * own to point at. This element stands in for the declaration: it is anchored at the parameter name inside the
 * owning [directive], which gives navigation a target, Find Usages an owner and rename an element to work on —
 * all confined to that single directive, because a macro parameter is not visible anywhere else.
 */
class IsPreprocessorMacroParameterElement(
    private val directive: IsPreprocessorDirective,
    private val parameterName: String,
    /** Offset of the parameter name inside [directive]'s text. */
    private val offsetInDirective: Int,
) : FakePsiElement() {

    /** The directive declaring this parameter — the element's parent for the platform. */
    override fun getParent(): PsiElement = directive

    /** The file the declaration lives in (the injected ISPP fragment). */
    override fun getContainingFile(): PsiFile = directive.containingFile

    /** The declared parameter name. */
    override fun getName(): String = parameterName

    /** Range of the parameter name inside the containing file. */
    override fun getTextRange(): TextRange =
        TextRange(
            directive.textRange.startOffset + offsetInDirective,
            directive.textRange.startOffset + offsetInDirective + parameterName.length,
        )

    /** Start offset of the parameter name inside the containing file. */
    override fun getTextOffset(): Int = textRange.startOffset

    /** The parameter name as its own text. */
    override fun getText(): String = parameterName

    /** Length of the parameter name. */
    override fun getTextLength(): Int = parameterName.length

    /** Whether [another] denotes the same parameter of the same macro. */
    override fun isEquivalentTo(another: PsiElement?): Boolean {
        val other = another as? IsPreprocessorMacroParameterElement ?: return false
        return other.directive == directive && other.parameterName == parameterName
    }

    /** Presentation used by Find Usages and the navigation popups. */
    override fun getPresentation(): ItemPresentation = object : ItemPresentation {
        override fun getPresentableText(): String = parameterName
        override fun getLocationString(): String =
            (directive as? IsPreprocessorDirectiveEx)?.getDefineName()?.let { "#define $it" } ?: "#define"

        override fun getIcon(unused: Boolean): Icon? = null
    }

    /** Renames the declaration in the document; the uses are renamed through their own references. */
    override fun setName(name: String): PsiElement {
        val injMgr = InjectedLanguageManager.getInstance(directive.project)
        val range = TextRange(offsetInDirective, offsetInDirective + parameterName.length)
            .shiftRight(directive.textRange.startOffset)
        val hostRange = injMgr.injectedToHost(directive, range)
        val hostFile = injMgr.getTopLevelFile(directive.containingFile)
        val docManager = PsiDocumentManager.getInstance(directive.project)
        val document = docManager.getDocument(hostFile) ?: return this
        docManager.doPostponedOperationsAndUnblockDocument(document)
        document.replaceString(hostRange.startOffset, hostRange.endOffset, name)
        docManager.commitDocument(document)
        return this
    }
}

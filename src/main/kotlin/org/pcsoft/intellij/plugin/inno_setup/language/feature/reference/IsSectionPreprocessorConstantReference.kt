/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.isppDirectives
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionConstantBody
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes

// Anchor is IsSectionConstantBody (not the IDENTIFIER leaf) so that getReferences() on the
// mixin element is the source of truth. TextRange skips the leading '#' character.
/**
 * Represents a PSI reference used for navigation, rename, and find-usages support.
 */
class IsSectionPreprocessorConstantReference(constantBody: IsSectionConstantBody, private val name: String) :
    PsiReferenceBase<IsSectionConstantBody>(constantBody, TextRange(1, 1 + name.length), true) {

    /**
     * Resolves this reference to its target PSI element, or `null` when unresolved.
     */
    override fun resolve(): PsiElement? {
        val issFile = element.containingFile as? IsScriptFile ?: return null
        return issFile.isppDirectives
            .firstOrNull { x ->
                (x as? IsPreprocessorDirectiveEx)?.isDefine() == true &&
                        x.getDefineName() == name
            }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isReferenceTo(element: PsiElement): Boolean {
        val resolved = resolve() ?: return false
        val mgr = element.manager
        if (mgr.areElementsEquivalent(resolved, element)) return true
        val nameId = (resolved as? PsiNameIdentifierOwner)?.nameIdentifier
        return nameId != null && mgr.areElementsEquivalent(nameId, element)
    }

    /**
     * Returns completion variants available from this reference.
     */
    override fun getVariants(): Array<Any> = emptyArray()

    /**
     * Updates the referenced text after the target element has been renamed.
     */
    override fun handleElementRename(newElementName: String): PsiElement {
        val oldId = element.node.findChildByType(IsSectionTypes.IDENTIFIER)?.psi ?: return element
        // Create a dummy ISS file with {#Name} in value context to get an IsSectionTypes.IDENTIFIER node.
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText(
                "dummy.iss", IsScriptFileType.INSTANCE,
                "[Setup]\nAppName={#$newElementName}\n"
            )
        val newId = PsiTreeUtil.findChildOfType(dummy, IsSectionConstantBody::class.java)
            ?.node?.findChildByType(IsSectionTypes.IDENTIFIER)?.psi ?: return element
        oldId.replace(newId)
        return element
    }
}

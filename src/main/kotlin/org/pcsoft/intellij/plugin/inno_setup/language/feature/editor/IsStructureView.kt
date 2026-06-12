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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.displayName
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.isParameterSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.sections
import javax.swing.Icon

class IsStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        val issFile = psiFile as? IsScriptFile ?: return null
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel =
                IsStructureViewModel(issFile, editor)
        }
    }
}

// The editor must be forwarded to StructureViewModelBase: getCurrentEditorElement() (and with it
// the structure-aware navigation bar's getLeafElement()) returns null when the model has no editor,
// which is why the navbar showed only the file and no [Section]/parameter member.
class IsStructureViewModel(file: IsScriptFile, editor: Editor? = null) :
    StructureViewModelBase(file, editor, IsStructureViewElement(file)),
    StructureViewModel.ElementInfoProvider {

    override fun getSuitableClasses(): Array<Class<*>> =
        arrayOf(IsSectionBlock::class.java, IsSectionParameterEntry::class.java, IsSectionDirectiveEntry::class.java)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean =
        element.value is IsSectionParameterEntry || element.value is IsSectionDirectiveEntry
}

class IsStructureViewElement(private val element: PsiElement) : StructureViewTreeElement {

    override fun getValue(): Any = element

    override fun getPresentation(): ItemPresentation = when (element) {
        is IsScriptFile -> SimpleItemPresentation(element.name, IsIcons.ScriptFile)
        is IsSectionBlock -> SimpleItemPresentation(element.nameText, IsIcons.Section)
        is IsSectionParameterEntry -> SimpleItemPresentation(element.displayName, IsIcons.ParameterEntry)
        is IsSectionDirectiveEntry -> SimpleItemPresentation(element.keyText(), IsIcons.ParameterEntry)
        else -> SimpleItemPresentation(element.text ?: "", null)
    }

    override fun getChildren(): Array<TreeElement> = when (element) {
        is IsScriptFile -> element.sections.map { IsStructureViewElement(it) }.toTypedArray()
        is IsSectionBlock -> if (element.isParameterSection)
            element.parameterEntryList.map { IsStructureViewElement(it) }.toTypedArray<TreeElement>()
        else element.directiveEntryList.map { IsStructureViewElement(it) }.toTypedArray<TreeElement>()

        else -> emptyArray()
    }

    override fun navigate(requestFocus: Boolean) {
        (element as? com.intellij.psi.NavigatablePsiElement)?.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean =
        (element as? com.intellij.psi.NavigatablePsiElement)?.canNavigate() ?: false

    override fun canNavigateToSource(): Boolean =
        (element as? com.intellij.psi.NavigatablePsiElement)?.canNavigateToSource() ?: false
}

private class SimpleItemPresentation(
    private val text: String,
    private val icon: Icon?
) : ItemPresentation {
    override fun getPresentableText(): String = text
    override fun getIcon(unused: Boolean): Icon? = icon
}

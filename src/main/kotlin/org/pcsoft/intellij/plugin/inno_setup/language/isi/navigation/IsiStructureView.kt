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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.displayName
import org.pcsoft.intellij.plugin.inno_setup.language.isi.isParameterSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.sections
import javax.swing.Icon

class IsiStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        val issFile = psiFile as? IssFile ?: return null
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel =
                IssStructureViewModel(issFile)
        }
    }
}

class IssStructureViewModel(file: IssFile) : StructureViewModelBase(file, IssStructureViewElement(file)),
    StructureViewModel.ElementInfoProvider {

    override fun getSuitableClasses(): Array<Class<*>> =
        arrayOf(IsiSection::class.java, IsiParameterEntry::class.java)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean =
        element.value is IsiParameterEntry
}

class IssStructureViewElement(private val element: PsiElement) : StructureViewTreeElement {

    override fun getValue(): Any = element

    override fun getPresentation(): ItemPresentation = when (element) {
        is IssFile -> SimpleItemPresentation(element.name, IssIcons.ScriptFile)
        is IsiSection -> SimpleItemPresentation(element.nameText(), IssIcons.Section)
        is IsiParameterEntry -> SimpleItemPresentation(element.displayName(), IssIcons.ParameterEntry)
        else -> SimpleItemPresentation(element.text ?: "", null)
    }

    override fun getChildren(): Array<TreeElement> = when (element) {
        is IssFile -> element.sections().map { IssStructureViewElement(it) }.toTypedArray()
        is IsiSection -> if (element.isParameterSection())
            element.parameterEntryList.map { IssStructureViewElement(it) }.toTypedArray<TreeElement>()
        else emptyArray()

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

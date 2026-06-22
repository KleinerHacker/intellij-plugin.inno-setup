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
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.sections
import javax.swing.Icon

/**
 * Provides Structure tool window support for Inno Setup script files.
 */
class IsStructureViewFactory : PsiStructureViewFactory {
    /**
     * Returns structure-view metadata for the supplied PSI element.
     */
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
/**
 * Provides Structure tool window support for Inno Setup script files.
 */
class IsStructureViewModel(file: IsScriptFile, editor: Editor? = null) :
    StructureViewModelBase(file, editor, IsStructureViewElement(file)),
    StructureViewModel.ElementInfoProvider {

    /**
     * Provides Inno Setup plugin behavior for the IntelliJ Platform.
     */
    override fun getSuitableClasses(): Array<Class<*>> =
        arrayOf(IsSectionBlock::class.java, IsSectionParameterEntry::class.java, IsSectionDirectiveEntry::class.java)

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

    /**
     * Returns presentation metadata used by IntelliJ navigation UI.
     */
    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean =
        element.value is IsSectionParameterEntry || element.value is IsSectionDirectiveEntry
}

/**
 * Provides Structure tool window support for Inno Setup script files.
 */
class IsStructureViewElement(private val element: PsiElement) : StructureViewTreeElement {

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getValue(): Any = element

    /**
     * Returns structure-view metadata for the supplied PSI element.
     */
    override fun getPresentation(): ItemPresentation = when (element) {
        is IsScriptFile -> SimpleItemPresentation(element.name, IsIcons.ScriptFile)
        is IsSectionBlock -> SimpleItemPresentation(element.nameText, IsIcons.Section)
        is IsSectionParameterEntry -> SimpleItemPresentation(element.displayName, IsIcons.ParameterEntry)
        is IsSectionDirectiveEntry -> SimpleItemPresentation(element.keyText(), IsIcons.ParameterEntry)
        else -> SimpleItemPresentation(element.text ?: "", null)
    }

    /**
     * Returns structure-view metadata for the supplied PSI element.
     */
    override fun getChildren(): Array<TreeElement> = when (element) {
        is IsScriptFile -> element.sections.map { IsStructureViewElement(it) }.toTypedArray()
        is IsSectionBlock -> if (element.isParameterSection)
            element.parameterEntryList.map { IsStructureViewElement(it) }.toTypedArray<TreeElement>()
        else element.directiveEntryList.map { IsStructureViewElement(it) }.toTypedArray<TreeElement>()

        else -> emptyArray()
    }

    /**
     * Returns structure-view metadata for the supplied PSI element.
     */
    override fun navigate(requestFocus: Boolean) {
        (element as? com.intellij.psi.NavigatablePsiElement)?.navigate(requestFocus)
    }

    /**
     * Returns structure-view metadata for the supplied PSI element.
     */
    override fun canNavigate(): Boolean =
        (element as? com.intellij.psi.NavigatablePsiElement)?.canNavigate() ?: false

    /**
     * Returns structure-view metadata for the supplied PSI element.
     */
    override fun canNavigateToSource(): Boolean =
        (element as? com.intellij.psi.NavigatablePsiElement)?.canNavigateToSource() ?: false
}

private class SimpleItemPresentation(
    private val text: String,
    private val icon: Icon?
) : ItemPresentation {
    /**
     * Returns presentation metadata used by IntelliJ navigation UI.
     */
    override fun getPresentableText(): String = text
    /**
     * Returns the icon shown for this element or file type.
     */
    override fun getIcon(unused: Boolean): Icon? = icon
}

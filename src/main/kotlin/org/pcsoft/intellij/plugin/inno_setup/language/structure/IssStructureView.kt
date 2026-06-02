package org.pcsoft.intellij.plugin.inno_setup.language.structure

import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.language.*
import org.pcsoft.intellij.plugin.inno_setup.language.psi.*
import javax.swing.Icon

class IssStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        val issFile = psiFile as? IssFile ?: return null
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel =
                IssStructureViewModel(issFile)
        }
    }
}

class IssStructureViewModel(file: IssFile)
    : StructureViewModelBase(file, IssStructureViewElement(file)),
      StructureViewModel.ElementInfoProvider {

    override fun getSuitableClasses(): Array<Class<*>> =
        arrayOf(IssSection::class.java, IssParameterEntry::class.java)

    override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean = false

    override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean =
        element.value is IssParameterEntry
}

class IssStructureViewElement(private val element: PsiElement) : StructureViewTreeElement {

    override fun getValue(): Any = element

    override fun getPresentation(): ItemPresentation = when (element) {
        is IssFile           -> SimpleItemPresentation(element.name, IssIcons.ScriptFile)
        is IssSection        -> SimpleItemPresentation(element.nameText(), IssIcons.Section)
        is IssParameterEntry -> SimpleItemPresentation(element.displayName(), IssIcons.ParameterEntry)
        else                 -> SimpleItemPresentation(element.text ?: "", null)
    }

    override fun getChildren(): Array<TreeElement> = when (element) {
        is IssFile    -> element.sections().map { IssStructureViewElement(it) }.toTypedArray()
        is IssSection -> if (element.isParameterSection())
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

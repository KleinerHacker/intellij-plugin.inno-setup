package org.pcsoft.plugins.intellij.inno_setup.script.structure.tree;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: Christoph
 * Date: 14.07.13
 * Time: 11:32
 * TODO: Write Summary
 */
public final class IssPsiStructureViewElement implements StructureViewTreeElement, SortableTreeElement {

    private PsiElement element;
    private boolean allowChildren;

    public IssPsiStructureViewElement(PsiElement element) {
        this(element, true);
    }

    public IssPsiStructureViewElement(PsiElement element, boolean allowChildren) {
        this.element = element;
        this.allowChildren = allowChildren;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean b) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(b);
        }
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem &&
                ((NavigationItem) element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem &&
                ((NavigationItem) element).canNavigateToSource();
    }

    @Override
    public String getAlphaSortKey() {
        return element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return element instanceof NavigationItem ?
                ((NavigationItem) element).getPresentation() : null;
    }

    @Override
    public TreeElement[] getChildren() {
        if (!allowChildren) {
            return StructureViewTreeElement.EMPTY_ARRAY;
        }

        if (element instanceof IssFile) {
            final Collection<IssSectionElement> sectionElements = PsiTreeUtil.findChildrenOfType(element, IssSectionElement.class);
            if (sectionElements == null || sectionElements.isEmpty())
                return StructureViewTreeElement.EMPTY_ARRAY;

            final List<TreeElement> treeElementList = sectionElements.stream()
                    .map(IssPsiStructureViewElement::new)
                    .collect(Collectors.toList());
            return treeElementList.toArray(new TreeElement[treeElementList.size()]);
        } else if (element instanceof IssDefinableSectionElement) {
            final Collection<IssDefinitionElement> definitionList = ((IssDefinableSectionElement) element).getDefinitionList();
            if (definitionList == null || definitionList.isEmpty())
                return StructureViewTreeElement.EMPTY_ARRAY;

            final List<TreeElement> treeElementList = definitionList.stream()
                    .map(definitionElement -> new IssPsiStructureViewElement(definitionElement, false))
                    .collect(Collectors.toList());
            return treeElementList.toArray(new TreeElement[treeElementList.size()]);
        }

        return StructureViewTreeElement.EMPTY_ARRAY;
    }
}

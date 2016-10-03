package org.pcsoft.plugins.intellij.iss.language.structure.tree;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssFile;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionLine;
import org.pcsoft.plugins.intellij.iss.language.type.SectionVariant;

import java.util.Collection;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private PsiElement element;

    public IssStructureViewElement(PsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        return element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return element instanceof NavigationItem ?
                ((NavigationItem) element).getPresentation() : null;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof IssFile) {
            final Collection<IssSection> sections = PsiTreeUtil.findChildrenOfType(element, IssSection.class);
            return sections.stream()
                    .map(IssStructureViewElement::new)
                    .toArray(TreeElement[]::new);
        } else if (element instanceof IssSection && ((IssSection) element).getSectionType().getVariant() == SectionVariant.LineBased) {
            final Collection<IssSectionLine> sectionLines = PsiTreeUtil.findChildrenOfType(element, IssSectionLine.class);
            return sectionLines.stream()
                    .map(IssStructureViewElement::new)
                    .toArray(TreeElement[]::new);
        }

        return EMPTY_ARRAY;
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
}

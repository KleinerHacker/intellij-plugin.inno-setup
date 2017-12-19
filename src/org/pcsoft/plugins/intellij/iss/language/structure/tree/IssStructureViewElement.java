package org.pcsoft.plugins.intellij.iss.language.structure.tree;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssFile;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSectionLine;
import org.pcsoft.plugins.intellij.iss.language.type.SectionVariant;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by Christoph on 03.10.2016.
 */
public class IssStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private static final ItemPresentation nullPresentation = new ItemPresentation() {
        @Nullable
        @Override
        public String getPresentableText() {
            return null;
        }

        @Nullable
        @Override
        public String getLocationString() {
            return null;
        }

        @Nullable
        @Override
        public Icon getIcon(boolean b) {
            return null;
        }
    };

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
        return element instanceof PsiNamedElement
                ? (String) ObjectUtils.defaultIfNull(((PsiNamedElement) element).getName(), "")
                : "";
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return element instanceof NavigationItem
                ? (ItemPresentation) ObjectUtils.defaultIfNull(((NavigationItem) element).getPresentation(), nullPresentation)
                : nullPresentation;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof IssFile) {
            final Collection<IssSection> sections = PsiTreeUtil.findChildrenOfType(element, IssSection.class);
            return sections.stream()
                    .map(IssStructureViewElement::new)
                    .toArray(TreeElement[]::new);
        } else if (element instanceof IssSection && ((IssSection) element).getSectionType() != null && ((IssSection) element).getSectionType().getVariant() == SectionVariant.MultiValue) {
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

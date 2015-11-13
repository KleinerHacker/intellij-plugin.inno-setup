package org.pcsoft.plugins.intellij.inno_setup.script.structure.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssSectionFoldingBuilder extends FoldingBuilderEx {

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(PsiElement psiElement, Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<IssSectionElement> sectionElements = PsiTreeUtil.findChildrenOfType(psiElement, IssSectionElement.class);
        for (final IssSectionElement sectionElement : sectionElements) {
            final int startOffset = sectionElement.getSectionNameElement() == null ? 0 :
                    sectionElement.getSectionNameElement().getTextRange().getEndOffset();
            final int endOffset = sectionElement.getTextRange().getEndOffset() - 1;
            if (endOffset <= startOffset)
                continue;

            list.add(new FoldingDescriptor(sectionElement, new TextRange(startOffset, endOffset)));
        }

        return list.toArray(new FoldingDescriptor[list.size()]);
    }

    @Override
    public boolean isCollapsedByDefault(ASTNode astNode) {
        return false;
    }

    @Nullable
    @Override
    public String getPlaceholderText(ASTNode astNode) {
        return "...";
    }
}

package org.pcsoft.plugins.intellij.iss.language.structure.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IssSectionFoldingBuilder extends FoldingBuilderEx {
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement psiElement, @NotNull Document document, boolean b) {
        final List<FoldingDescriptor> list = new ArrayList<>();

        final Collection<IssSection> sections = PsiTreeUtil.findChildrenOfType(psiElement, IssSection.class);
        for (final IssSection section : sections) {
            final int startOffset = section.getTextRange().getStartOffset();
            final int endOffset = section.getTextRange().getEndOffset() - 1;
            if (endOffset <= startOffset)
                continue;

            list.add(new FoldingDescriptor(section, new TextRange(startOffset, endOffset)));
        }

        return list.toArray(new FoldingDescriptor[list.size()]);
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode astNode) {
        return ((IssSection) astNode.getPsi()).getSectionTitle().getText() + "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode astNode) {
        return false;
    }
}

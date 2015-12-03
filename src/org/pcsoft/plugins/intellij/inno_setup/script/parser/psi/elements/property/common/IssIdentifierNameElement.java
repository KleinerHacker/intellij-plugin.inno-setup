package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.EmptyIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssLanguageDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssCustomMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssMessageSectionElement;

import javax.swing.*;

/**
 * Created by Christoph on 30.11.2015.
 */
public class IssIdentifierNameElement extends IssAbstractElement implements PsiNameIdentifierOwner {

    public IssIdentifierNameElement(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return getParentProperty() == null ? null : getParentProperty().getSection() == null ? null :
                        getParentProperty().getSection().getSectionType() == null ? null :
                                getParentProperty().getSection().getSectionType().getId();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIdentifierNameElement.this.getIcon(0);
            }
        };
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        if (getParentProperty() != null && getParentProperty().getSection() != null &&
                (getParentProperty().getSection() instanceof IssMessageSectionElement ||
                        getParentProperty().getSection() instanceof IssCustomMessageSectionElement)) {
            if (getParentProperty().getIdentifier() != null && getParentProperty().getIdentifier().getIdentifierReference() != null &&
                    getParentProperty().getIdentifier().getIdentifierReference().getReference() != null &&
                    getParentProperty().getIdentifier().getIdentifierReference().getReference().resolve() != null) {
                final PsiElement psiElement = getParentProperty().getIdentifier().getIdentifierReference().getReference().resolve();
                if (!(psiElement instanceof IssPropertyNameValueElement))
                    return new EmptyIcon(16, 16);
                final IssLanguageDefinitionElement languageDefinitionElement = PsiTreeUtil.getParentOfType(psiElement, IssLanguageDefinitionElement.class);
                if (languageDefinitionElement == null || languageDefinitionElement.getLanguageType() == null)
                    return new EmptyIcon(16, 16);

                return languageDefinitionElement.getLanguageType().getFlagIcon();
            }
        }

        return new EmptyIcon(16, 16);
    }

    @Nullable
    public IssPropertyElement getParentProperty() {
        return PsiTreeUtil.getParentOfType(this, IssPropertyElement.class);
    }
}

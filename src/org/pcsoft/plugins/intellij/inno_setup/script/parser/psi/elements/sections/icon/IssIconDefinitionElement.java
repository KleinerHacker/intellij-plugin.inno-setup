package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyDescriptionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;

import javax.swing.*;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssIconDefinitionElement extends IssDefinitionElement<IssIconSectionElement, IssIconProperty> {

    public IssIconDefinitionElement(ASTNode node) {
        super(node, IssIconSectionElement.class);
    }

    @NotNull
    @Override
    public IssIconProperty[] getPropertyTypeList() {
        return IssIconProperty.values();
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
                return "Icons";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return null;
            }
        };
    }

    @Override
    public String getName() {
        return getTypeName() == null ? null : getTypeName().getPropertyValue() == null ? null : getTypeName().getPropertyValue().getName();
    }

    @Nullable
    public IssIconPropertyNameElement getTypeName() {
        return PsiTreeUtil.findChildOfType(this, IssIconPropertyNameElement.class);
    }

    @Nullable
    public IssTypePropertyFlagsElement getTypeFlags() {
        return PsiTreeUtil.findChildOfType(this, IssTypePropertyFlagsElement.class);
    }
}

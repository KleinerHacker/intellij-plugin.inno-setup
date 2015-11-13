package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTypeFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssTypeSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssTypeDefinitionElement extends IssDefinitionElement<IssTypeSectionElement, IssTypeProperty> {

    public IssTypeDefinitionElement(ASTNode node) {
        super(node, IssTypeSectionElement.class);
    }

    @NotNull
    @Override
    public IssTypeProperty[] getPropertyTypeList() {
        return IssTypeProperty.values();
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
                return "Types";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return IssIcons.IC_SECT_TYPE;
            }
        };
    }

    @Nullable
    @Override
    public String getName() {
        return getTypeName() == null ? null : getTypeName().getPropertyValue() == null ? null : getTypeName().getPropertyValue().getName();
    }

    @Nullable
    public IssPropertyNameElement getTypeName() {
        return IssFindUtils.findProperty(this, IssTypeProperty.Name, IssPropertyNameElement.class);
    }

    @Nullable
    public IssPropertyStringElement getTypeDescription() {
        return IssFindUtils.findProperty(this, IssTypeProperty.Description, IssPropertyStringElement.class);
    }

    @Nullable
    public IssPropertyTypeFlagsElement getTypeFlags() {
        return IssFindUtils.findProperty(this, IssTypeProperty.Flags, IssPropertyTypeFlagsElement.class);
    }
}

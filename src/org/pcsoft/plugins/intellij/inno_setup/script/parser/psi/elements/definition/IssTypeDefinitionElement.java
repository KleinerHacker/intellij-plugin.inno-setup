package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTypeFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssTypeSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

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

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.Type;
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

package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOAttributeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssIOAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIOAttributePropertyValueCompletionContributor extends IssAbstractPropertyValueCompletionContributor<IssPropertyIOAttributeElement> {
    public IssIOAttributePropertyValueCompletionContributor() {
        super(IssPropertyIOAttributeElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getValueList() {
        return IssIOAttribute.values();
    }

    @Override
    public Icon getIcon(IssPropertyValue propertyValue) {
        return IssIcons.IC_INFO_ATTRIBUTE;
    }
}

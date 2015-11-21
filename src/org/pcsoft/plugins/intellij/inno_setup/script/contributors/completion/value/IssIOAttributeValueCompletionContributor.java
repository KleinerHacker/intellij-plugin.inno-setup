package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyIOAttributeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIOAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIOAttributeValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyIOAttributeElement> {
    public IssIOAttributeValueCompletionContributor() {
        super(IssPropertyIOAttributeElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssIOAttribute.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_ATTRIBUTE;
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIconFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssIconFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIconFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyIconFlagsElement> {
    public IssIconFlagValueCompletionContributor() {
        super(IssPropertyIconFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssIconFlag.values();
    }

    @Override
    public Icon getIcon(IssPropertyValue propertyValue) {
        return IssIcons.IC_INFO_FLAG;
    }
}

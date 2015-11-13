package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIconFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIconFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyIconFlagsElement> {
    public IssIconFlagValueCompletionContributor() {
        super(IssPropertyIconFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssIconFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

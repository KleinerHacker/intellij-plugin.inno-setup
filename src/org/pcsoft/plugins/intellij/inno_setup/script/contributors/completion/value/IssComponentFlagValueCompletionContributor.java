package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssComponentFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyComponentFlagsElement> {
    public IssComponentFlagValueCompletionContributor() {
        super(IssPropertyComponentFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssComponentFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

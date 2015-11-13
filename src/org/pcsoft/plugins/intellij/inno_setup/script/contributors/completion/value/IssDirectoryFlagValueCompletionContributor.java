package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDirectoryFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDirectoryFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssDirectoryFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyDirectoryFlagsElement> {
    public IssDirectoryFlagValueCompletionContributor() {
        super(IssPropertyDirectoryFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssDirectoryFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

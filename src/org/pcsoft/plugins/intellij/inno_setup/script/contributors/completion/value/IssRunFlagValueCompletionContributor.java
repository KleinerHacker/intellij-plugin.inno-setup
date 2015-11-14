package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRunFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRunFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssRunFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyRunFlagsElement> {
    public IssRunFlagValueCompletionContributor() {
        super(IssPropertyRunFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssRunFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

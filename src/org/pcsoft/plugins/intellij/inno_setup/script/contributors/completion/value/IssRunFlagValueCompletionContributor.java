package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyRunFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;
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
    protected IssPropertyValue[] getFlagList() {
        return IssRunFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

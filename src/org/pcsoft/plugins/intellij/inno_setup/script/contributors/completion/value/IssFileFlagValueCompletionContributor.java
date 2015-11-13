package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyFileFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFileFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyFileFlagsElement> {
    public IssFileFlagValueCompletionContributor() {
        super(IssPropertyFileFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssFileFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

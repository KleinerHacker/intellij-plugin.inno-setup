package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyDirectoryFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDirectoryFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssDirectoryFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyDirectoryFlagsElement> {
    public IssDirectoryFlagValueCompletionContributor() {
        super(IssPropertyDirectoryFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssDirectoryFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyUninstallRunFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssUninstallRunFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssUninstallRunFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyUninstallRunFlagsElement> {
    public IssUninstallRunFlagValueCompletionContributor() {
        super(IssPropertyUninstallRunFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssUninstallRunFlag.values();
    }

    @Override
    protected Icon getIcon(IssPropertyValue propertyValue) {
        return IssIcons.IC_INFO_FLAG;
    }
}

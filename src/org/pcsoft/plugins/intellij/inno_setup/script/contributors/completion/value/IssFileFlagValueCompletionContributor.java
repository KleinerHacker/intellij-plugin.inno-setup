package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyFileFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFileFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyFileFlagsElement> {
    public IssFileFlagValueCompletionContributor() {
        super(IssPropertyFileFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssFileFlag.values();
    }

    @Override
    protected Icon getIcon(IssPropertyValue propertyValue) {
        return IssIcons.IC_INFO_FLAG;
    }
}

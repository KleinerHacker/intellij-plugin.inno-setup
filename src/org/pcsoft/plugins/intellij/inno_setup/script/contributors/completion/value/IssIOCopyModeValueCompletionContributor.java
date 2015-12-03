package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOCopyModeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssFileCopyMode;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIOCopyModeValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyIOCopyModeElement> {
    public IssIOCopyModeValueCompletionContributor() {
        super(IssPropertyIOCopyModeElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssFileCopyMode.values();
    }

    @Override
    public Icon getIcon(IssPropertyValue propertyValue) {
        return IssIcons.IC_INFO_COPYMODE;
    }
}

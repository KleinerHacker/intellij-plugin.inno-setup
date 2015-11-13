package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOCopyModeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileCopyMode;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIOCopyModeValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyIOCopyModeElement> {
    public IssIOCopyModeValueCompletionContributor() {
        super(IssPropertyIOCopyModeElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssFileCopyMode.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_COPYMODE;
    }
}

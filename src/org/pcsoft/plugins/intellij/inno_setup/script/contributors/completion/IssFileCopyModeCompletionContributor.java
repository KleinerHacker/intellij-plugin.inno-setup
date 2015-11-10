package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyCopyModeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileCopyMode;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFileCopyModeCompletionContributor extends IssAbstractFlagCompletionContributor<IssFilePropertyCopyModeElement> {
    public IssFileCopyModeCompletionContributor() {
        super(IssFilePropertyCopyModeElement.class);
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

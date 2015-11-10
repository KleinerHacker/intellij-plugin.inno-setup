package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFileFlagCompletionContributor extends IssAbstractFlagCompletionContributor<IssFilePropertyFlagsElement> {
    public IssFileFlagCompletionContributor() {
        super(IssFilePropertyFlagsElement.class);
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

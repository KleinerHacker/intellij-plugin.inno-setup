package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDirectoryFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssDirectoryFlagCompletionContributor extends IssAbstractFlagCompletionContributor<IssDirectoryPropertyFlagsElement> {
    public IssDirectoryFlagCompletionContributor() {
        super(IssDirectoryPropertyFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssDirectoryFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

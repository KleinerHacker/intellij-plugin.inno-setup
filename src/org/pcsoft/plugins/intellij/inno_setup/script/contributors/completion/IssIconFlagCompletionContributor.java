package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon.IssIconPropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIconFlagCompletionContributor extends IssAbstractFlagCompletionContributor<IssIconPropertyFlagsElement> {
    public IssIconFlagCompletionContributor() {
        super(IssIconPropertyFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssIconFlag.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_FLAG;
    }
}

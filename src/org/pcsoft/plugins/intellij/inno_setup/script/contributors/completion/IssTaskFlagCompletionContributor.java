package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskFlagCompletionContributor extends IssAbstractFlagCompletionContributor<IssTaskPropertyFlagsElement> {
    public IssTaskFlagCompletionContributor() {
        super(IssTaskPropertyFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssTaskFlag.values();
    }
}

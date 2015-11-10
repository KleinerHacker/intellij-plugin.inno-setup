package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypeFlagCompletionContributor extends IssAbstractFlagCompletionContributor<IssTypePropertyFlagsElement> {
    public IssTypeFlagCompletionContributor() {
        super(IssTypePropertyFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssTypeFlag.values();
    }
}

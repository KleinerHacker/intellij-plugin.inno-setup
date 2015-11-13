package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTypeFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypeFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyTypeFlagsElement> {
    public IssTypeFlagValueCompletionContributor() {
        super(IssPropertyTypeFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssTypeFlag.values();
    }
}

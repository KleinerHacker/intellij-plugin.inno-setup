package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTypeFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypeFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyTypeFlagsElement> {
    public IssTypeFlagValueCompletionContributor() {
        super(IssPropertyTypeFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssTypeFlag.values();
    }
}

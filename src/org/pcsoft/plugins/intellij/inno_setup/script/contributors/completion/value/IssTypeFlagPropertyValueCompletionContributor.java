package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTypeFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssTypeFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypeFlagPropertyValueCompletionContributor extends IssAbstractPropertyValueCompletionContributor<IssPropertyTypeFlagsElement> {
    public IssTypeFlagPropertyValueCompletionContributor() {
        super(IssPropertyTypeFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getValueList() {
        return IssTypeFlag.values();
    }
}

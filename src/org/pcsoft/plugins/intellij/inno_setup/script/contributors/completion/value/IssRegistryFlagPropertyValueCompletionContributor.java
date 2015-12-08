package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssRegistryFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssRegistryFlagPropertyValueCompletionContributor extends IssAbstractPropertyValueCompletionContributor<IssPropertyRegistryFlagsElement> {
    public IssRegistryFlagPropertyValueCompletionContributor() {
        super(IssPropertyRegistryFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getValueList() {
        return IssRegistryFlag.values();
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyRegistryFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssRegistryFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyRegistryFlagsElement> {
    public IssRegistryFlagValueCompletionContributor() {
        super(IssPropertyRegistryFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssRegistryFlag.values();
    }
}

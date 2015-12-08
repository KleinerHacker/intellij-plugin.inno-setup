package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryRootElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssRegistryRoot;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssRegistryRootPropertyValueCompletionContributor extends IssAbstractPropertyValueCompletionContributor<IssPropertyRegistryRootElement> {
    public IssRegistryRootPropertyValueCompletionContributor() {
        super(IssPropertyRegistryRootElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getValueList() {
        return IssRegistryRoot.values();
    }
}

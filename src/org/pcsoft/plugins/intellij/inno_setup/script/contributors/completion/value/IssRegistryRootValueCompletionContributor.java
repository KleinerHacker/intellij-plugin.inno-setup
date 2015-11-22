package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyRegistryRootElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryRoot;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssRegistryRootValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyRegistryRootElement> {
    public IssRegistryRootValueCompletionContributor() {
        super(IssPropertyRegistryRootElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssRegistryRoot.values();
    }
}

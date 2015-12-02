package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryValueTypeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssRegistryValueType;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssRegistryValueTypeValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyRegistryValueTypeElement> {
    public IssRegistryValueTypeValueCompletionContributor() {
        super(IssPropertyRegistryValueTypeElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssRegistryValueType.values();
    }
}

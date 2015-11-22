package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyRegistryValueTypeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryValueType;

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
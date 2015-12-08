package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyINIFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssINIFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssINIFlagPropertyValueCompletionContributor extends IssAbstractPropertyValueCompletionContributor<IssPropertyINIFlagsElement> {
    public IssINIFlagPropertyValueCompletionContributor() {
        super(IssPropertyINIFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getValueList() {
        return IssINIFlag.values();
    }
}

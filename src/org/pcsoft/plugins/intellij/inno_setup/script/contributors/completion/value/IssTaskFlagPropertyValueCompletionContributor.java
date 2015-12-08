package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssTaskFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskFlagPropertyValueCompletionContributor extends IssAbstractPropertyValueCompletionContributor<IssPropertyTaskFlagsElement> {
    public IssTaskFlagPropertyValueCompletionContributor() {
        super(IssPropertyTaskFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getValueList() {
        return IssTaskFlag.values();
    }
}

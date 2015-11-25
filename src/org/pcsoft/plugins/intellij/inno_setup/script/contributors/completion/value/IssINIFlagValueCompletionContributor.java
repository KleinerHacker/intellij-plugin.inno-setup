package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyINIFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssINIFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssINIFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyINIFlagsElement> {
    public IssINIFlagValueCompletionContributor() {
        super(IssPropertyINIFlagsElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssINIFlag.values();
    }
}

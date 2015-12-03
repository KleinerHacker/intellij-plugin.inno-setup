package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDeleteTypeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssDeleteType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssDeleteTypeValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyDeleteTypeElement> {
    public IssDeleteTypeValueCompletionContributor() {
        super(IssPropertyDeleteTypeElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssDeleteType.values();
    }
}

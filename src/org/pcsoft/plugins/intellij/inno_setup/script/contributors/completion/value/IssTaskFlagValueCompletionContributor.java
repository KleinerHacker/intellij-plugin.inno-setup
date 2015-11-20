package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTaskFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskFlagValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyTaskFlagsElement> {
    public IssTaskFlagValueCompletionContributor() {
        super(IssPropertyTaskFlagsElement.class);
    }

    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssTaskFlag.values();
    }
}

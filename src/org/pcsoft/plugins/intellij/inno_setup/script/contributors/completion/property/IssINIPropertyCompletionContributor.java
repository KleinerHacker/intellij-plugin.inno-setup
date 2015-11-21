package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssINISectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssINIProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssINIPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssINISectionElement> {
    public IssINIPropertyCompletionContributor() {
        super(IssINISectionElement.class);
    }

    @NotNull
    @Override
    protected IssDefinablePropertyIdentifier[] getSectionIdentifierList() {
        return IssINIProperty.values();
    }
}

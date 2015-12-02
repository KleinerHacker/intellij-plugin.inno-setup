package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssINISectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssINIProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssINIPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssINISectionElement> {
    public IssINIPropertyCompletionContributor() {
        super(IssINISectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssINIProperty.values();
    }
}

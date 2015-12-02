package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssTaskSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssTaskProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssTaskSectionElement> {
    public IssTaskPropertyCompletionContributor() {
        super(IssTaskSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssTaskProperty.values();
    }
}

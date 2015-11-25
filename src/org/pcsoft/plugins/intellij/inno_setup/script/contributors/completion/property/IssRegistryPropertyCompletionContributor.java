package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssRegistrySectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssRegistryPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssRegistrySectionElement> {
    public IssRegistryPropertyCompletionContributor() {
        super(IssRegistrySectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssRegistryProperty.values();
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssUninstallDeleteSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssUninstallDeleteProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssUninstallDeletePropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssUninstallDeleteSectionElement> {
    public IssUninstallDeletePropertyCompletionContributor() {
        super(IssUninstallDeleteSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssUninstallDeleteProperty.values();
    }
}

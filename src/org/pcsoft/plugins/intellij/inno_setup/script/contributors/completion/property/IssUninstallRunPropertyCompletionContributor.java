package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssUninstallRunSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssUninstallRunProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssUninstallRunPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssUninstallRunSectionElement> {
    public IssUninstallRunPropertyCompletionContributor() {
        super(IssUninstallRunSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssUninstallRunProperty.values();
    }
}

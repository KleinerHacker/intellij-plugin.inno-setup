package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssInstallRunSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssInstallRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssInstallRunPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssInstallRunSectionElement> {
    public IssInstallRunPropertyCompletionContributor() {
        super(IssInstallRunSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssInstallRunProperty.values();
    }
}

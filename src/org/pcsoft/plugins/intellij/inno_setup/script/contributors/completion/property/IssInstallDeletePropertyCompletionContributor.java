package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssInstallDeleteSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssInstallDeleteProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssInstallDeletePropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssInstallDeleteSectionElement> {
    public IssInstallDeletePropertyCompletionContributor() {
        super(IssInstallDeleteSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssInstallDeleteProperty.values();
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssInstallRunSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssInstallRunProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssInstallRunPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssInstallRunSectionElement> {
    public IssInstallRunPropertyCompletionContributor() {
        super(IssInstallRunSectionElement.class);
    }

    @NotNull
    @Override
    protected IssDefinablePropertyIdentifier[] getSectionIdentifierList() {
        return IssInstallRunProperty.values();
    }
}

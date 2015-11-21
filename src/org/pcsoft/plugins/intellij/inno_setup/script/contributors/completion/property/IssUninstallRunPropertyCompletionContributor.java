package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssUninstallRunSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssUninstallRunProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssUninstallRunPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssUninstallRunSectionElement> {
    public IssUninstallRunPropertyCompletionContributor() {
        super(IssUninstallRunSectionElement.class);
    }

    @Override
    protected IssDefinablePropertyIdentifier[] getSectionIdentifierList() {
        return IssUninstallRunProperty.values();
    }
}

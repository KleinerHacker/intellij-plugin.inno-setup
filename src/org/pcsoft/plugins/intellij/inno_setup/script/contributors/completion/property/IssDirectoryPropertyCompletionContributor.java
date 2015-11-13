package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssDirectorySectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDirectoryProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssDirectoryPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssDirectorySectionElement> {
    public IssDirectoryPropertyCompletionContributor() {
        super(IssDirectorySectionElement.class);
    }

    @Override
    protected IssDefinableSectionIdentifier[] getSectionIdentifierList() {
        return IssDirectoryProperty.values();
    }
}

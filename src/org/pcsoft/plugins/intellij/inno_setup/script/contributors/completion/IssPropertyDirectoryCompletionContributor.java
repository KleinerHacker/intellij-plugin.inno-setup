package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectorySectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDirectoryProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssPropertyDirectoryCompletionContributor extends IssPropertyCompletionContributor<IssDirectorySectionElement> {
    public IssPropertyDirectoryCompletionContributor() {
        super(IssDirectorySectionElement.class);
    }

    @Override
    protected IssDefinableSectionIdentifier[] getSectionIdentifierList() {
        return IssDirectoryProperty.values();
    }
}

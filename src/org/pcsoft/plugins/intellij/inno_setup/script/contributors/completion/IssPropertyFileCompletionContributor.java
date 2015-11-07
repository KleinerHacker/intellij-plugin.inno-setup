package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssPropertyFileCompletionContributor extends IssPropertyCompletionContributor<IssFileSectionElement> {
    public IssPropertyFileCompletionContributor() {
        super(IssFileSectionElement.class);
    }

    @Override
    protected IssDefinableSectionIdentifier[] getSectionIdentifierList() {
        return IssFileProperty.values();
    }
}

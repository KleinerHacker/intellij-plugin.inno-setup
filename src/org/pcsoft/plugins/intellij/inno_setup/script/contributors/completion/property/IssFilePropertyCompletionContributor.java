package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssFileSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFilePropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssFileSectionElement> {
    public IssFilePropertyCompletionContributor() {
        super(IssFileSectionElement.class);
    }

    @Override
    protected IssDefinableSectionIdentifier[] getSectionIdentifierList() {
        return IssFileProperty.values();
    }
}

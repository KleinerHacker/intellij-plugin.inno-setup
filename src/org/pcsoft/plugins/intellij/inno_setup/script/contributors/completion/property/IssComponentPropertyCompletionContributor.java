package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssComponentSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssComponentPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssComponentSectionElement> {
    public IssComponentPropertyCompletionContributor() {
        super(IssComponentSectionElement.class);
    }

    @Override
    protected IssDefinableSectionIdentifier[] getSectionIdentifierList() {
        return IssComponentProperty.values();
    }
}

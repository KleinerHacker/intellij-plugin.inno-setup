package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssRunSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRunProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssRunPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssRunSectionElement> {
    public IssRunPropertyCompletionContributor() {
        super(IssRunSectionElement.class);
    }

    @Override
    protected IssDefinableSectionIdentifier[] getSectionIdentifierList() {
        return IssRunProperty.values();
    }
}

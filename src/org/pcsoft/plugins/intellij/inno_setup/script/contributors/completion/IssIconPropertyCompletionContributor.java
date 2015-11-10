package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon.IssIconSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIconPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssIconSectionElement> {
    public IssIconPropertyCompletionContributor() {
        super(IssIconSectionElement.class);
    }

    @Override
    protected IssDefinableSectionIdentifier[] getSectionIdentifierList() {
        return IssIconProperty.values();
    }
}

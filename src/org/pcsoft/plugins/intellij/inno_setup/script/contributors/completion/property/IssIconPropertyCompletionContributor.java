package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssIconSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIconPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssIconSectionElement> {
    public IssIconPropertyCompletionContributor() {
        super(IssIconSectionElement.class);
    }

    @Override
    protected IssDefinablePropertyIdentifier[] getSectionIdentifierList() {
        return IssIconProperty.values();
    }
}

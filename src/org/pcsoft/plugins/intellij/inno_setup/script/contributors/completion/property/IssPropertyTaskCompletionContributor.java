package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssTaskSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinablePropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssPropertyTaskCompletionContributor extends IssAbstractPropertyCompletionContributor<IssTaskSectionElement> {
    public IssPropertyTaskCompletionContributor() {
        super(IssTaskSectionElement.class);
    }

    @Override
    protected IssDefinablePropertyIdentifier[] getSectionIdentifierList() {
        return IssTaskProperty.values();
    }
}

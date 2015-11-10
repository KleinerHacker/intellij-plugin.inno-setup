package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssPropertyTypeCompletionContributor extends IssAbstractPropertyCompletionContributor<IssTypeSectionElement> {
    public IssPropertyTypeCompletionContributor() {
        super(IssTypeSectionElement.class);
    }

    @Override
    protected IssDefinableSectionIdentifier[] getSectionIdentifierList() {
        return IssTypeProperty.values();
    }
}

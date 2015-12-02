package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssTypeSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssTypeProperty;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypePropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssTypeSectionElement> {
    public IssTypePropertyCompletionContributor() {
        super(IssTypeSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssTypeProperty.values();
    }
}

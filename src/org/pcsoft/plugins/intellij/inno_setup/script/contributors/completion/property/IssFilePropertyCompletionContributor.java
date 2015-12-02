package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssFileSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssFileProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFilePropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssFileSectionElement> {
    public IssFilePropertyCompletionContributor() {
        super(IssFileSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssFileProperty.values();
    }
}

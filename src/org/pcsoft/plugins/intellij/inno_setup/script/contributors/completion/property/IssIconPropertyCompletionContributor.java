package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssIconSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIconProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssIconPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssIconSectionElement> {
    public IssIconPropertyCompletionContributor() {
        super(IssIconSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssIconProperty.values();
    }
}

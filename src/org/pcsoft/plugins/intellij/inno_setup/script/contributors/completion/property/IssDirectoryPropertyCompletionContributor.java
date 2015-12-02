package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssDirectorySectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssDirectoryProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssDirectoryPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssDirectorySectionElement> {
    public IssDirectoryPropertyCompletionContributor() {
        super(IssDirectorySectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssDirectoryProperty.values();
    }
}

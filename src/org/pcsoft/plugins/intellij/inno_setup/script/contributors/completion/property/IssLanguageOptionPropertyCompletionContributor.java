package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssLanguageOptionSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssLanguageOptionProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssLanguageOptionPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssLanguageOptionSectionElement> {
    public IssLanguageOptionPropertyCompletionContributor() {
        super(IssLanguageOptionSectionElement.class, PropertyType.Standard);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssLanguageOptionProperty.values();
    }
}

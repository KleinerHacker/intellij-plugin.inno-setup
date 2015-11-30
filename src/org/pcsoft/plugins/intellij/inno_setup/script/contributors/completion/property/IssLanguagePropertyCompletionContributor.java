package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssLanguageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssLanguageProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssLanguagePropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssLanguageSectionElement> {
    public IssLanguagePropertyCompletionContributor() {
        super(IssLanguageSectionElement.class, PropertyType.Definable);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssLanguageProperty.values();
    }
}

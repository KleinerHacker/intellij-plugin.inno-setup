package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssSetupSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssPropertyIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssSetupProperty;

import java.awt.Color;
import java.awt.Font;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssSetupPropertyCompletionContributor extends IssAbstractPropertyCompletionContributor<IssSetupSectionElement> {
    public IssSetupPropertyCompletionContributor() {
        super(IssSetupSectionElement.class, PropertyType.Standard);
    }

    @NotNull
    @Override
    protected IssPropertyIdentifier[] getSectionIdentifierList() {
        return IssSetupProperty.values();
    }

    @Nullable
    @Override
    public String getTypeText(IssPropertyIdentifier propertyIdentifier) {
        return ((IssSetupProperty)propertyIdentifier).getClassifier().getText();
    }

    @Override
    public boolean getBoldness(IssPropertyIdentifier propertyIdentifier) {
        return ((IssSetupProperty)propertyIdentifier).getClassifier().getTextAttributesKey().getDefaultAttributes().getFontType() == Font.BOLD;
    }

    @NotNull
    @Override
    public Color getTextColor(IssPropertyIdentifier propertyIdentifier) {
        return ((IssSetupProperty)propertyIdentifier).getClassifier().getTextAttributesKey().getDefaultAttributes().getForegroundColor();
    }
}

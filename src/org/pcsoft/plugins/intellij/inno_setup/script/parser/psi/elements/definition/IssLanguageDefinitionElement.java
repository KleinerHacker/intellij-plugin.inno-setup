package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyStringElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssLanguageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssLanguageProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssLanguageType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.Icon;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssLanguageDefinitionElement extends IssDefinitionElement<IssLanguageSectionElement, IssLanguageProperty> {

    public IssLanguageDefinitionElement(ASTNode node) {
        super(node, IssLanguageSectionElement.class);
    }

    @NotNull
    @Override
    public IssLanguageProperty[] getPropertyTypeList() {
        return IssLanguageProperty.values();
    }

    @Nullable
    @Override
    protected String getDefinitionName() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getName());
        if (getLanguageMessageFile() != null && getLanguageMessageFile().getPropertyValue() != null) {
            sb.append(" (").append(getLanguageMessageFile().getPropertyValue().getString()).append(")");
        }
        return sb.toString();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.Language;
    }

    @Nullable
    @Override
    protected Icon getPresentableIcon() {
        final IssLanguageType languageType = getLanguageType();
        return languageType == null ? super.getPresentableIcon() : languageType.getFlagIcon();
    }

    @Nullable
    public IssLanguageType getLanguageType() {
        if (getLanguageMessageFile() == null || getLanguageMessageFile().getPropertyValue() == null)
            return null;

        final IssLanguageType languageType = IssLanguageType.findByFile(getLanguageMessageFile().getPropertyValue().getString().replace("compiler:", ""));
        if (languageType == null || languageType.getFlagIcon() == null)
            return null;

        return languageType;
    }

    @Nullable
    @Override
    public String getName() {
        return getLanguageName() == null ? null : getLanguageName().getPropertyValue() == null ? null : getLanguageName().getPropertyValue().getName();
    }

    @Nullable
    public IssPropertyNameElement getLanguageName() {
        return IssFindUtils.findProperty(this, IssLanguageProperty.Name, IssPropertyNameElement.class);
    }

    @Nullable
    public IssPropertyStringElement getLanguageMessageFile() {
        return IssFindUtils.findProperty(this, IssLanguageProperty.MessageFile, IssPropertyStringElement.class);
    }

}

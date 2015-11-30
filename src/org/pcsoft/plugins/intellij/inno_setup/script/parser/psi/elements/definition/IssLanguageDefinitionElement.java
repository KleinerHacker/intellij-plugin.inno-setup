package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssLanguageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssLanguageProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssLanguageDefinitionElement extends IssDefinitionElement<IssLanguageSectionElement, IssTypeProperty> {

    public IssLanguageDefinitionElement(ASTNode node) {
        super(node, IssLanguageSectionElement.class);
    }

    @NotNull
    @Override
    public IssTypeProperty[] getPropertyTypeList() {
        return IssTypeProperty.values();
    }

    @Nullable
    @Override
    protected String getDefinitionName() {
        return getName();
    }

    @NotNull
    @Override
    protected IssSectionType getSectionType() {
        return IssSectionType.Type;
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
}

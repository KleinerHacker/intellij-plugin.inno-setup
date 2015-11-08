package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentPropertyExtraDiskSpaceRequiredElement extends IssDefinitionPropertyElement<IssComponentDefinitionElement, IssComponentPropertyExtraDiskSpaceRequiredValueElement> {
    public IssComponentPropertyExtraDiskSpaceRequiredElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class, IssComponentPropertyExtraDiskSpaceRequiredValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.Integer;
    }
}

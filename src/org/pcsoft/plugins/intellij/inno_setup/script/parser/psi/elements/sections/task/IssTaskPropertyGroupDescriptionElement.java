package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskPropertyGroupDescriptionElement extends IssDefinitionPropertyElement<IssTaskDefinitionElement,IssTaskPropertyGroupDescriptionValueElement> {
    public IssTaskPropertyGroupDescriptionElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class,IssTaskPropertyGroupDescriptionValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.String;
    }
}

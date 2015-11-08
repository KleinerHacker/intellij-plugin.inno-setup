package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssDefinitionPropertyDefaultElement extends IssDefinitionPropertyElement<IssDefinitionElement, IssDefinitionPropertyDefaultValueElement> {
    public IssDefinitionPropertyDefaultElement(ASTNode node) {
        super(node, IssDefinitionElement.class, IssDefinitionPropertyDefaultValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.Unknown;
    }
}

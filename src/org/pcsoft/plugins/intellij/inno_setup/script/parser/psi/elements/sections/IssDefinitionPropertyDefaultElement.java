package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections;

import com.intellij.lang.ASTNode;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssDefinitionPropertyDefaultElement extends IssDefinitionPropertyElement<IssDefinitionElement> {

    public IssDefinitionPropertyDefaultElement(ASTNode node) {
        super(node, IssDefinitionElement.class);
    }
}

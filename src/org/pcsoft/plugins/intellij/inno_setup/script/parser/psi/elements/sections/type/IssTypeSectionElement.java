package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinableSectionElement;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssTypeSectionElement extends IssDefinableSectionElement<IssTypeDefinitionElement> {
    public IssTypeSectionElement(ASTNode node) {
        super(node, IssTypeDefinitionElement.class);
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinableSectionElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskSectionElement extends IssDefinableSectionElement<IssTaskDefinitionElement> {

    public IssTaskSectionElement(ASTNode node) {
        super(node, IssTaskDefinitionElement.class);
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTypeDefinitionElement;

import javax.swing.*;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssTypeSectionElement extends IssDefinableSectionElement<IssTypeDefinitionElement> {
    public IssTypeSectionElement(ASTNode node) {
        super(node, IssTypeDefinitionElement.class);
    }
}

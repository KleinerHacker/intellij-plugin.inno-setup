package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssIconDefinitionElement;

import javax.swing.*;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssIconSectionElement extends IssDefinableSectionElement<IssIconDefinitionElement> {
    public IssIconSectionElement(ASTNode node) {
        super(node, IssIconDefinitionElement.class);
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssComponentDefinitionElement;

import javax.swing.*;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentSectionElement extends IssDefinableSectionElement<IssComponentDefinitionElement> {

    public IssComponentSectionElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class);
    }
}

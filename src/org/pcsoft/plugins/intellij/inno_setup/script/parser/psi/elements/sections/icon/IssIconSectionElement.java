package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinableSectionElement;

import javax.swing.*;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssIconSectionElement extends IssDefinableSectionElement<IssIconDefinitionElement> {
    public IssIconSectionElement(ASTNode node) {
        super(node, IssIconDefinitionElement.class);
    }

    @Override
    protected Icon getIcon(boolean b) {
        return IssIcons.IC_SECT_ICON;
    }
}

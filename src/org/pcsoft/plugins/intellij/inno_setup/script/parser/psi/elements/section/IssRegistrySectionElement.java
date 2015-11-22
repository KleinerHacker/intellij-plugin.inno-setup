package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssRegistryDefinitionElement;

import javax.swing.*;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssRegistrySectionElement extends IssDefinableSectionElement<IssRegistryDefinitionElement> {
    public IssRegistrySectionElement(ASTNode node) {
        super(node, IssRegistryDefinitionElement.class);
    }

    @Override
    protected Icon getIcon(boolean b) {
        return IssIcons.IC_SECT_TYPE;
    }
}

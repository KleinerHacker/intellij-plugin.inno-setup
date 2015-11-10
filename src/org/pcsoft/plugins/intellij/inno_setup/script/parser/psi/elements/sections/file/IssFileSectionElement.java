package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinableSectionElement;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFileSectionElement extends IssDefinableSectionElement<IssFileDefinitionElement> {

    public IssFileSectionElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class);
    }

    @Override
    protected Icon getIcon(boolean b) {
        return IssIcons.IC_SECT_FILE;
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssInstallRunDefinitionElement;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssInstallRunSectionElement extends IssDefinableSectionElement<IssInstallRunDefinitionElement> {

    public IssInstallRunSectionElement(ASTNode node) {
        super(node, IssInstallRunDefinitionElement.class);
    }

    @Override
    protected Icon getIcon(boolean b) {
        return IssIcons.IC_SECT_INSTALL_RUN;
    }
}

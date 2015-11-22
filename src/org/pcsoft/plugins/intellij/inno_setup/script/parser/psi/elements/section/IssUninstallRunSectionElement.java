package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssUninstallRunDefinitionElement;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssUninstallRunSectionElement extends IssDefinableSectionElement<IssUninstallRunDefinitionElement> {

    public IssUninstallRunSectionElement(ASTNode node) {
        super(node, IssUninstallRunDefinitionElement.class);
    }
}

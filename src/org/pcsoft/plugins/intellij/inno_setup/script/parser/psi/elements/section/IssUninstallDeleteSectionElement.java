package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssUninstallDeleteDefinitionElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssUninstallDeleteSectionElement extends IssDefinableSectionElement<IssUninstallDeleteDefinitionElement> {

    public IssUninstallDeleteSectionElement(ASTNode node) {
        super(node, IssUninstallDeleteDefinitionElement.class);
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssInstallDeleteDefinitionElement;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssInstallDeleteSectionElement extends IssDefinableSectionElement<IssInstallDeleteDefinitionElement> {

    public IssInstallDeleteSectionElement(ASTNode node) {
        super(node, IssInstallDeleteDefinitionElement.class);
    }
}

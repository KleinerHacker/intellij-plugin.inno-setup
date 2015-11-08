package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssDirectorySectionElement extends IssDefinableSectionElement<IssDirectoryDefinitionElement> {

    public IssDirectorySectionElement(ASTNode node) {
        super(node, IssDirectoryDefinitionElement.class);
    }
}

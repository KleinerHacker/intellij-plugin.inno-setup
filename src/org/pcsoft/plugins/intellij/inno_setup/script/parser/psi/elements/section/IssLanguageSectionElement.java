package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinableSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssLanguageDefinitionElement;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssLanguageSectionElement extends IssDefinableSectionElement<IssLanguageDefinitionElement> {
    public IssLanguageSectionElement(ASTNode node) {
        super(node, IssLanguageDefinitionElement.class);
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentPropertyDescriptionValueElement extends IssDefinitionItemValueElement {

    public IssComponentPropertyDescriptionValueElement(ASTNode node) {
        super(node);
    }

    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.String;
    }
}

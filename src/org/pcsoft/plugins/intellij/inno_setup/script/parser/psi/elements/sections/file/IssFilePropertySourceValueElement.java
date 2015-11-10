package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssFilePropertySourceValueElement extends IssDefinitionPropertyValueElement {

    public IssFilePropertySourceValueElement(ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText().replace("\"", "");
    }
}

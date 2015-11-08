package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssDirectoryPropertyAttributeValueElement extends IssDefinitionPropertyValueElement {

    public IssDirectoryPropertyAttributeValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }
}

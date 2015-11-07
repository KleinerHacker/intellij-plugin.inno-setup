package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssFilePropertyFlagsValueElement extends IssAbstractElement {

    public IssFilePropertyFlagsValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public class IssMessageConstantElement extends IssConstantElement {

    public IssMessageConstantElement(ASTNode node) {
        super(node);
    }

    @NotNull
    public Collection<IssConstantArgumentElement> getMessageArgumentList() {
        return getConstantArgumentList();
    }
}

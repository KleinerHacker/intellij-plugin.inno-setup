package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssSectionNameElement extends IssAbstractElement {

    public IssSectionNameElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return this.getText().replace("[", "").replace("]", "").trim();
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryRoot;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyRegistryRootValueElement extends IssPropertyValueElement {

    public IssPropertyRegistryRootValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssRegistryRoot getRootType() {
        return IssRegistryRoot.fromId(getText());
    }
}

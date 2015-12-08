package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssRegistryConstantElement extends IssConstantElement {

    public IssRegistryConstantElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssConstantNameElement getRegistryPath() {
        return getConstantName();
    }

    @Nullable
    public IssConstantArgumentElement getRegistryValueName() {
        return getConstantArgumentList().size() < 1 ? null : new ArrayList<>(getConstantArgumentList()).get(0);
    }
}

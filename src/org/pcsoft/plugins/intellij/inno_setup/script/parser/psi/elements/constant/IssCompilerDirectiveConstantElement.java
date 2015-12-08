package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssCompilerDirectiveConstantElement extends IssConstantElement {

    public IssCompilerDirectiveConstantElement(ASTNode node) {
        super(node);
    }
}

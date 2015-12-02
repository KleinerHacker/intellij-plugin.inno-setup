package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

import java.util.Collection;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssCompilerDirectiveParametersElement extends IssAbstractElement {

    public IssCompilerDirectiveParametersElement(ASTNode node) {
        super(node);
    }

    @NotNull
    public Collection<IssCompilerDirectiveParameterElement> getValueList() {
        return PsiTreeUtil.findChildrenOfType(this, IssCompilerDirectiveParameterElement.class);
    }
}

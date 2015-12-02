package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveParameterIdentifier;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public abstract class IssCompilerDirectiveParameterElement extends IssAbstractElement {
    private final IssCompilerDirectiveParameterIdentifier parameterType;

    public IssCompilerDirectiveParameterElement(ASTNode node, IssCompilerDirectiveParameterIdentifier parameterType) {
        super(node);
        this.parameterType = parameterType;
    }

    @Nullable
    public final IssCompilerDirectiveSectionElement getCompilerDirectiveSection() {
        return PsiTreeUtil.getParentOfType(this, IssCompilerDirectiveSectionElement.class);
    }

    @NotNull
    public final IssCompilerDirectiveParameterIdentifier getParameterType() {
        return parameterType;
    }
}

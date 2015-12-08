package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSymbolUndefineSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.references.IssCompilerDirectiveSymbolDefineReference;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveParameterIdentifier;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssCompilerDirectiveIdentifierParameterElement extends IssCompilerDirectiveParameterElement implements PsiNameIdentifierOwner {

    public IssCompilerDirectiveIdentifierParameterElement(ASTNode node, IssCompilerDirectiveParameterIdentifier parameterType) {
        super(node, parameterType);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiReference getReference() {
        if (getCompilerDirectiveSection() instanceof IssCompilerDirectiveSymbolUndefineSectionElement) {
            return new IssCompilerDirectiveSymbolDefineReference(this, true);
        }

        return null;
    }
}

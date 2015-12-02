package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.IssCompilerDirectiveSectionIdentifier;

import java.util.Collection;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public abstract class IssCompilerDirectiveSectionElement extends IssAbstractElement {
    public IssCompilerDirectiveSectionElement(ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getCompilerDirective() == null ? null : getCompilerDirective().getName();
    }

    @Nullable
    public final IssCompilerDirectiveElement getCompilerDirective() {
        return PsiTreeUtil.findChildOfType(this, IssCompilerDirectiveElement.class);
    }

    @NotNull
    public final Collection<IssCompilerDirectiveParameterElement> getParameterList() {
        return PsiTreeUtil.findChildrenOfType(this, IssCompilerDirectiveParameterElement.class);
    }

    @NotNull
    public abstract IssCompilerDirectiveSectionIdentifier getSectionType();
}

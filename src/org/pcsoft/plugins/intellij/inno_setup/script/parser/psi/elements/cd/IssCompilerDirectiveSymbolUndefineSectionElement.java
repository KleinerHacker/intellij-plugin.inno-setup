package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter.IssCompilerDirectiveIdentifierParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter.IssCompilerDirectiveVisibilityParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.IssCompilerDirectiveSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.IssCompilerDirectiveSectionType;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssCompilerDirectiveSymbolUndefineSectionElement extends IssCompilerDirectiveSectionElement {

    public IssCompilerDirectiveSymbolUndefineSectionElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssCompilerDirectiveIdentifierParameterElement getSymbolName() {
        return PsiTreeUtil.findChildOfType(this, IssCompilerDirectiveIdentifierParameterElement.class);
    }

    @NotNull
    @Override
    public IssCompilerDirectiveSectionIdentifier getSectionType() {
        return IssCompilerDirectiveSectionType.SymbolUndefine;
    }

    @Nullable
    public IssCompilerDirectiveVisibilityParameterElement getVisibilityParameter() {
        return PsiTreeUtil.findChildOfType(this, IssCompilerDirectiveVisibilityParameterElement.class);
    }

    @Nullable
    public IssCompilerDirectiveIdentifierParameterElement getIdentifierParameter() {
        return PsiTreeUtil.findChildOfType(this, IssCompilerDirectiveIdentifierParameterElement.class);
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter.IssCompilerDirectiveIncludeFileParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.IssCompilerDirectiveSectionIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.IssCompilerDirectiveSectionType;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssCompilerDirectiveIncludeSectionElement extends IssCompilerDirectiveSectionElement {

    public IssCompilerDirectiveIncludeSectionElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssCompilerDirectiveIncludeFileParameterElement getIncludeFile() {
        return PsiTreeUtil.findChildOfType(this, IssCompilerDirectiveIncludeFileParameterElement.class);
    }

    @NotNull
    @Override
    public IssCompilerDirectiveSectionIdentifier getSectionType() {
        return IssCompilerDirectiveSectionType.Include;
    }
}

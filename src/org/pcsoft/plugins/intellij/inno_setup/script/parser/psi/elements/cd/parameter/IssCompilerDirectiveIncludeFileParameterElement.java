package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveIncludeParameterType;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssCompilerDirectiveIncludeFileParameterElement extends IssCompilerDirectiveParameterElement {

    public IssCompilerDirectiveIncludeFileParameterElement(ASTNode node) {
        super(node, IssCompilerDirectiveIncludeParameterType.File);
    }

    @NotNull

    @Override
    public String getName() {
        return getText().replace("<", "").replace(">", "").replace("\"", "");
    }
}

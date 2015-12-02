package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveParameterIdentifier;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssCompilerDirectiveStringParameterElement extends IssCompilerDirectiveParameterElement {

    public IssCompilerDirectiveStringParameterElement(ASTNode node, IssCompilerDirectiveParameterIdentifier parameterType) {
        super(node, parameterType);
    }

    @NotNull
    @Override
    public String getName() {
        return getText().replace("\"", "");
    }
}

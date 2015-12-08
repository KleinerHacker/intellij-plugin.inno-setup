package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value.IssCompilerDirectiveVisibility;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveParameterIdentifier;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssCompilerDirectiveVisibilityParameterElement extends IssCompilerDirectiveParameterElement {

    public IssCompilerDirectiveVisibilityParameterElement(ASTNode node, IssCompilerDirectiveParameterIdentifier parameterType) {
        super(node, parameterType);
    }

    @Nullable
    public IssCompilerDirectiveVisibility getCompilerDirectiveVisibility() {
        return IssCompilerDirectiveVisibility.fromId(getText());
    }
}

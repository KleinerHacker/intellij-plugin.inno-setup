package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectivePreProcessorParameterType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value.IssCompilerDirectivePreProcessorType;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssCompilerDirectivePreProcessorTypeParameterElement extends IssCompilerDirectiveParameterElement {

    public IssCompilerDirectivePreProcessorTypeParameterElement(ASTNode node) {
        super(node, IssCompilerDirectivePreProcessorParameterType.Type);
    }

    @Nullable
    public IssCompilerDirectivePreProcessorType getCompilerDirectivePreProcessorType() {
        return IssCompilerDirectivePreProcessorType.fromId(getText());
    }
}

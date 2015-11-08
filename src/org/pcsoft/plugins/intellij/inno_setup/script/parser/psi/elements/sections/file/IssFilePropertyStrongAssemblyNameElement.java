package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFilePropertyStrongAssemblyNameElement extends IssDefinitionPropertyElement<IssFileDefinitionElement,IssFilePropertyStrongAssemblyNameValueElement> {
    public IssFilePropertyStrongAssemblyNameElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class,IssFilePropertyStrongAssemblyNameValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.String;
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssDirectoryPropertyNameElement extends IssDefinitionPropertyElement<IssDirectoryDefinitionElement,IssDirectoryPropertyNameValueElement> {
    public IssDirectoryPropertyNameElement(ASTNode node) {
        super(node, IssDirectoryDefinitionElement.class,IssDirectoryPropertyNameValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.String;
    }
}

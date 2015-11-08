package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssFilePropertyFlagsValueElement extends IssDefinitionPropertyValueElement {

    public IssFilePropertyFlagsValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.DirectMultiple;
    }
}

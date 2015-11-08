package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssTaskPropertyDescriptionValueElement extends IssDefinitionPropertyValueElement {

    public IssTaskPropertyDescriptionValueElement(ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText().replace("\"", "");
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.String;
    }
}

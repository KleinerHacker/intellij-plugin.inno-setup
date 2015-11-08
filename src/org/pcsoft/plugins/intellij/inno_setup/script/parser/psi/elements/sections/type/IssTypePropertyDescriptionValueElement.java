package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssTypePropertyDescriptionValueElement extends IssDefinitionPropertyValueElement {

    public IssTypePropertyDescriptionValueElement(ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getText().replace("\"", "");
    }
}

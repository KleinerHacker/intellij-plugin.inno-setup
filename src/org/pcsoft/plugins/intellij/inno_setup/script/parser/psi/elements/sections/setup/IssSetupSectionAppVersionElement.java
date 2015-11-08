package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssSetupSectionAppVersionElement extends IssDefinitionPropertyElement {

    public IssSetupSectionAppVersionElement(ASTNode node) {
        super(node, null, null);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.DirectSingle;
    }
}

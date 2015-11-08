package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentPropertyExtraDiskSpaceRequiredValueElement extends IssDefinitionPropertyValueElement {

    public IssComponentPropertyExtraDiskSpaceRequiredValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public Integer getSize() {
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

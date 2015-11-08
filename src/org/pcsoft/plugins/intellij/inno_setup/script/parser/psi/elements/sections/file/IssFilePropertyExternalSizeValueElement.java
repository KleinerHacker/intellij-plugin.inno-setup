package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssFilePropertyExternalSizeValueElement extends IssDefinitionPropertyValueElement {

    public IssFilePropertyExternalSizeValueElement(ASTNode node) {
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

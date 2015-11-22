package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssStandardSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.standard.IssPropertyCompressionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.standard.IssPropertyDefaultElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSetupProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssCustomMessageSectionElement extends IssStandardSectionElement {

    public IssCustomMessageSectionElement(ASTNode node) {
        super(node);
    }
}

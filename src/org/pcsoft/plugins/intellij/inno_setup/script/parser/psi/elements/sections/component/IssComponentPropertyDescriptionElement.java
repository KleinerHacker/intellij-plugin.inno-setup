package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssComponentPropertyDescriptionElement extends IssDefinitionPropertyElement<IssComponentDefinitionElement, IssDefinitionPropertyValueElement> {
    public IssComponentPropertyDescriptionElement(ASTNode node) {
        super(node, IssComponentDefinitionElement.class, IssDefinitionPropertyValueElement.class);
    }
}

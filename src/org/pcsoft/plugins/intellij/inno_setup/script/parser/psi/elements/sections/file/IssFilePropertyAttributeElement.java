package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

import java.util.Collection;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFilePropertyAttributeElement extends IssDefinitionPropertyElement<IssFileDefinitionElement,IssFilePropertyAttributeValueElement> {
    public IssFilePropertyAttributeElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class,IssFilePropertyAttributeValueElement.class);
    }
}

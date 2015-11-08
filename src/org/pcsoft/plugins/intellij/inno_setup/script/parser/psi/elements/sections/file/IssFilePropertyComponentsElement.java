package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

import java.util.Collection;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssFilePropertyComponentsElement extends IssDefinitionPropertyElement<IssFileDefinitionElement,IssFilePropertyComponentsValueElement> {
    public IssFilePropertyComponentsElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class,IssFilePropertyComponentsValueElement.class);
    }
}

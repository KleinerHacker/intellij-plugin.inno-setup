package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFilePropertyPermissionsElement extends IssDefinitionPropertyElement<IssFileDefinitionElement,IssFilePropertyPermissionsValueElement> {
    public IssFilePropertyPermissionsElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class,IssFilePropertyPermissionsValueElement.class);
    }
}

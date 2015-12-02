package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 20.11.2015.
 */
public class IssPropertyCompressionValueElement extends IssPropertyValueElement implements PsiNamedElement {

    public IssPropertyCompressionValueElement(ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(String s) throws IncorrectOperationException {
        return null;
    }
}

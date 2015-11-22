package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

/**
 * Created by Christoph on 14.12.2014.
 */
public abstract class IssAbstractElement extends ASTWrapperPsiElement {

    public IssAbstractElement(ASTNode node) {
        super(node);
    }
}

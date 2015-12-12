package org.pcsoft.plugins.intellij.inno_setup.script.quickfix;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.psi.PsiElement;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public abstract class IssAbstractQuickFix<T extends PsiElement> extends AbstractIntentionAction {
    protected final T myElement;

    public IssAbstractQuickFix(T myElement) {
        this.myElement = myElement;
    }


}

package org.pcsoft.plugins.intellij.inno_setup.script.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyAppIdElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyAppIdValueElement;
import org.pcsoft.plugins.intellij.inno_setup.utils.IssUtils;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public class IssGenerateAppIdAction extends AnAction {

    public IssGenerateAppIdAction() {
        super("Generate new App ID", "Generate a new App ID with 127 characters.", AllIcons.Actions.Refresh);
    }

    @Override
    public void update(AnActionEvent e) {
        final PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());

        e.getPresentation().setVisible(e.getProject() != null && psiElement != null &&
                (psiElement instanceof IssPropertyAppIdValueElement || PsiTreeUtil.getParentOfType(psiElement, IssPropertyAppIdValueElement.class) != null ||
                        PsiTreeUtil.getParentOfType(psiElement, IssPropertyAppIdElement.class) != null));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());
        final IssPropertyAppIdValueElement valueElement;
        if (psiElement instanceof IssPropertyAppIdValueElement) {
            valueElement = (IssPropertyAppIdValueElement) psiElement;
        } else if (PsiTreeUtil.getParentOfType(psiElement, IssPropertyAppIdElement.class) != null) {
            final IssPropertyAppIdElement appIdElement = PsiTreeUtil.getParentOfType(psiElement, IssPropertyAppIdElement.class);
            valueElement = appIdElement.getPropertyValue();
        } else if (PsiTreeUtil.getParentOfType(psiElement, IssPropertyAppIdValueElement.class) != null) {
            valueElement = PsiTreeUtil.getParentOfType(psiElement, IssPropertyAppIdValueElement.class);
        } else {
            valueElement = null;
        }
        if (valueElement == null)
            return;

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                new WriteCommandAction<Void>(valueElement.getProject(), valueElement.getContainingFile()) {
                    @Override
                    protected void run(Result<Void> result) throws Throwable {
                        valueElement.setName("{{" + IssUtils.generateAppId() + "}");
                    }
                }.performCommand();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}

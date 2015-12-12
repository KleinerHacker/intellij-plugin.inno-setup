package org.pcsoft.plugins.intellij.inno_setup.script.quickfix;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyAppIdValueElement;
import org.pcsoft.plugins.intellij.inno_setup.utils.IssUtils;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public class IssPropertyAppIdGenerationQuickFix extends IssAbstractQuickFix<IssPropertyAppIdValueElement> {

    public IssPropertyAppIdGenerationQuickFix(IssPropertyAppIdValueElement myElement) {
        super(myElement);
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Generate new App ID";
    }

    @Override
    public void invoke(Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        ApplicationManager.getApplication().runWriteAction(() -> {
            myElement.setName("{{" + IssUtils.generateAppId() + "}");
        });
    }
}

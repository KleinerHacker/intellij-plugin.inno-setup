package org.pcsoft.plugins.intellij.iss.ide.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssDefaultSectionLine;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssFile;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssProperty;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.element.IssSection;
import org.pcsoft.plugins.intellij.iss.language.type.SectionType;
import org.pcsoft.plugins.intellij.iss.language.type.property.SetupPropertyType;
import org.pcsoft.plugins.intellij.iss.util.IssUtils;

import java.util.stream.Stream;

public class IssGenerateAppIdAction extends AnAction {
    public IssGenerateAppIdAction() {
        super("Generate new App ID", "Generate a new App ID with 127 characters.", AllIcons.Actions.Refresh);
    }

    @Override
    public void update(AnActionEvent e) {
        final Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        final PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(e.getDataContext());
        final Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());

        e.getPresentation().setVisible(project != null && psiFile != null && psiFile instanceof IssFile && editor != null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            return;
        final PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(e.getDataContext());
        if (psiFile == null)
            return;
        final Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());
        if (editor == null)
            return;

        if (!(psiFile instanceof IssFile))
            return;
        final IssFile file = (IssFile)psiFile;
        if (file.getSections() == null || file.getSections().length <= 0)
            return;
        final IssSection section = Stream.of(file.getSections())
                .filter(issSection -> issSection.getSectionType() == SectionType.Setup)
                .findFirst().orElse(null);
        if (section == null)
            return;
        final IssProperty property = section.getDefaultSectionLineList().stream()
                .map(IssDefaultSectionLine::getDefaultProperty)
                .filter(issDefaultProperty -> issDefaultProperty.getPropertyType() == SetupPropertyType.AppId)
                .findFirst().orElse(null);
        if (property == null || property.getValue() == null)
            return;

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                new WriteCommandAction<Void>(project, psiFile) {
                    @Override
                    protected void run(@NotNull Result<Void> result) throws Throwable {
                        final int start = property.getValue().getTextRange().getStartOffset();
                        final int end = property.getValue().getTextRange().getEndOffset();

                        editor.getDocument().replaceString(start, end, "{{" + IssUtils.generateAppId() + "}");
                    }
                }.performCommand();
            } catch (Throwable throwable) {
                Logger.getInstance(IssGenerateAppIdAction.class).error("Unable to run app id write action", throwable);
            }
        });
    }
}

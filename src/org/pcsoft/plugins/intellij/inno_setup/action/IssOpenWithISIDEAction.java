package org.pcsoft.plugins.intellij.inno_setup.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.IssConstants;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.configuration.app.IssCompilerSettings;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssScriptFile;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssOpenWithISIDEAction extends AnAction {
    private static final IssCompilerSettings COMPILER_SETTINGS = ServiceManager.getService(IssCompilerSettings.class);

    public IssOpenWithISIDEAction() {
        super("Open with Inno Setup IDE", "Open the selected file with Inno Setup IDE, if set via configuration.", IssIcons.INNO_SETUP);
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final PsiElement psiElement = CommonDataKeys.PSI_ELEMENT.getData(e.getDataContext());

        e.getPresentation().setEnabled(COMPILER_SETTINGS.getInstallationPath() != null && COMPILER_SETTINGS.getInstallationPath().exists());
        e.getPresentation().setVisible(e.getProject() != null && virtualFile != null && virtualFile.getExtension() != null && virtualFile.getExtension().equalsIgnoreCase("iss") &&
                (psiElement == null || psiElement instanceof IssScriptFile));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        final File ideFile = new File(COMPILER_SETTINGS.getInstallationPath(), IssConstants.EXE_IDE);
        try {
            Runtime.getRuntime().exec("\"" + ideFile.getAbsolutePath() + "\" \"" + virtualFile.getPath() + "\"");
        } catch (IOException e1) {
            Logger.getInstance(IssOpenWithISIDEAction.class).error("Unable to start Inno Setup IDE", e1);
            JOptionPane.showMessageDialog(e.getInputEvent().getComponent(), "Unable to start Inno Setup IDE. Please check your settings!", "Inno Setup IDE", JOptionPane.ERROR_MESSAGE);
        }
    }
}

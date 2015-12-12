package org.pcsoft.plugins.intellij.inno_setup.action;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.lang.SystemUtils;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.ui.IssCreateScriptWizardDialog;
import org.pcsoft.plugins.intellij.inno_setup.utils.IssUtils;

import javax.swing.JOptionPane;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public class IssCreateScriptAction extends AnAction {

    public IssCreateScriptAction() {
        super("Inno Setup Script", "Generate a new Inno Setup Script with help of a wizard.", IssIcons.INNO_SETUP);
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        e.getPresentation().setVisible(e.getProject() != null && virtualFile != null && virtualFile.getFileType() != ModuleFileType.INSTANCE &&
                virtualFile.getFileType() != ProjectFileType.INSTANCE);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        final IssCreateScriptWizardDialog wizardDialog = new IssCreateScriptWizardDialog(e.getProject());
        final boolean success = wizardDialog.showAndGet();
        if (!success)
            return;

        final IssCreateScriptWizardDialog.Result result = wizardDialog.getResult();
        try (final Scanner scanner = new Scanner(getClass().getResourceAsStream("/templates/template.iss"))) {
            try (final DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(extractPath(virtualFile), result.getFilename() + ".iss")))) {
                String line;
                try {
                    while ((line = scanner.nextLine()) != null) {
                        out.writeUTF(replacePlaceholder(line, result) + SystemUtils.LINE_SEPARATOR);
                    }
                } catch (NoSuchElementException ex1) {
                    //Do nothing, exit point, no more lines
                }
            }
        } catch (IOException ex) {
            Logger.getInstance(IssCreateScriptAction.class).error("Unable to write file '" + result.getFilename() + "' from template!", ex);
            JOptionPane.showMessageDialog(e.getInputEvent().getComponent(), "Unable to write file: " + result.getFilename(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            VirtualFileManager.getInstance().syncRefresh();
        }
    }

    private static String replacePlaceholder(String line, IssCreateScriptWizardDialog.Result result) {
        if (line.contains("$APPID$")) {
            line = line.replace("$APPID$", IssUtils.generateAppId());
        }

        return line.replace("$APPNAME$", result.getAppName()).replace("$APPVERSION$", result.getAppVersion())
                .replace("$APPPUBLISHER$", result.getAppPublisher()).replace("$APPURL$", result.getAppPublisherURL())
                .replace("$SETUPNAME$", result.getSetupBaseFilename()).replace("$SETUPOUT$", result.getSetupOutputDirectory());
    }

    private static String extractPath(VirtualFile virtualFile) {
        if (virtualFile.isDirectory())
            return virtualFile.getPath();

        return virtualFile.getParent().getPath();
    }
}

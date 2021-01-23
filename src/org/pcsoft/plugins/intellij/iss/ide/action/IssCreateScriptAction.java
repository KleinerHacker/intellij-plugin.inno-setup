package org.pcsoft.plugins.intellij.iss.ide.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.lang.SystemUtils;
import org.pcsoft.plugins.intellij.iss.IssIcons;
import org.pcsoft.plugins.intellij.iss.ide.ui.IssCreateScriptWizardDialog;
import org.pcsoft.plugins.intellij.iss.module.IssModuleType;
import org.pcsoft.plugins.intellij.iss.util.IssUtils;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

public class IssCreateScriptAction extends AnAction {
    public IssCreateScriptAction() {
        super("Inno Setup Script", "Creates an inno setup script", IssIcons.FILE);
    }

    @Override
    public void update(AnActionEvent e) {
        final Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        final Module module = ModuleUtil.findModuleForFile(virtualFile, project);

        e.getPresentation().setVisible(module != null && Objects.equals(module.getModuleTypeName(), IssModuleType.ID) &&
                (ModuleRootManager.getInstance(module).getFileIndex().isInSourceContent(virtualFile) || ModuleRootManager.getInstance(module).getFileIndex().isInTestSourceContent(virtualFile)));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null)
            return;
        final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (virtualFile == null)
            return;

        final IssCreateScriptWizardDialog wizardDialog = new IssCreateScriptWizardDialog(e.getProject());
        final boolean success = wizardDialog.showAndGet();
        if (!success)
            return;

        final IssCreateScriptWizardDialog.Result result = wizardDialog.getResult();
        try (final Scanner scanner = new Scanner(getClass().getResourceAsStream("/templates/template.iss"), "UTF-8")) {
            try (final ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                try (final OutputStreamWriter out = new OutputStreamWriter(bout, "UTF-8")) {
                    String line;
                    try {
                        while ((line = scanner.nextLine()) != null) {
                            out.append(replacePlaceholder(line, result)).append(SystemUtils.LINE_SEPARATOR);
                        }
                    } catch (NoSuchElementException ex1) {
                        //Do nothing, exit point, no more lines
                    }
                }

                final VirtualFile targetFile = virtualFile.createChildData(null, result.getFilename() + ".iss");
                targetFile.setBinaryContent(bout.toByteArray());

                VirtualFileManager.getInstance().syncRefresh();
                new OpenFileDescriptor(project, targetFile).navigate(true);
            }
        } catch (IOException ex) {
            Logger.getInstance(IssCreateScriptAction.class).error("Unable to write file '" + result.getFilename() + "' from template!", ex);
            JOptionPane.showMessageDialog(e.getInputEvent().getComponent(), "Unable to write file: " + result.getFilename(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String replacePlaceholder(String line, IssCreateScriptWizardDialog.Result result) {
        if (line.contains("$APPID$")) {
            line = line.replace("$APPID$", IssUtils.generateAppId());
        }

        return line.replace("$APPNAME$", result.getAppName()).replace("$APPVERSION$", result.getAppVersion())
                .replace("$APPPUBLISHER$", result.getAppPublisher()).replace("$APPURL$", result.getAppPublisherURL())
                .replace("$SETUPNAME$", result.getSetupBaseFilename()).replace("$SETUPOUT$", "out");
    }

    private static String extractPath(VirtualFile virtualFile) {
        if (virtualFile.isDirectory())
            return virtualFile.getPath();

        return virtualFile.getParent().getPath();
    }
}

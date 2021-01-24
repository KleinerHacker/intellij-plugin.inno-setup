package org.pcsoft.plugins.intellij.iss.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.ide.action.IssCreateScriptAction;
import org.pcsoft.plugins.intellij.iss.module.model.IssModuleModel;
import org.pcsoft.plugins.intellij.iss.util.FileUtils;
import org.pcsoft.plugins.intellij.iss.util.IssUtils;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class IssModuleBuilder extends ModuleBuilder {
    private final IssModuleModel model = new IssModuleModel();

    @Override
    public ModuleType<?> getModuleType() {
        return IssModuleType.getInstance();
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new IssModuleWizardStep(model);
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        final ContentEntry contentEntry = doAddContentEntry(modifiableRootModel);
        final Module module = modifiableRootModel.getModule();
        final String moduleDir = ModuleUtil.getModuleDirPath(module);

        createFolder(moduleDir, "scripts/runtime", contentEntry, FolderType.Source);
        createFolder(moduleDir, "scripts/test", contentEntry, FolderType.Test);
        createFolder(moduleDir, "data", contentEntry, FolderType.Default);
        createFolder(moduleDir, "out", contentEntry, FolderType.Exclude);

        if (model.isCreateDefaultScript()) {
            final File runtimeFolderFile = new File(moduleDir, "scripts/runtime");

            try (final Scanner scanner = new Scanner(getClass().getResourceAsStream("/templates/template.iss"), "UTF-8")) {
                try (final ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                    try (final OutputStreamWriter out = new OutputStreamWriter(bout, "UTF-8")) {
                        String line;
                        try {
                            while ((line = scanner.nextLine()) != null) {
                                out.append(replacePlaceholder(line)).append(SystemUtils.LINE_SEPARATOR);
                            }
                        } catch (NoSuchElementException ex1) {
                            //Do nothing, exit point, no more lines
                        }
                    }

                    final File file = new File(runtimeFolderFile, model.getFilename() + ".iss");
                    FileUtils.writeByteArrayToFile(file, bout.toByteArray());
                }
            } catch (IOException ex) {
                Logger.getInstance(IssCreateScriptAction.class).error("Unable to write file '" + model.getFilename() + "' from template!", ex);
                JOptionPane.showMessageDialog(null, "Unable to write file: " + model.getFilename(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            LocalFileSystem.getInstance().refresh(false);
        }
    }

    private void createFolder(String rootDir, String folder, ContentEntry e, FolderType type) {
        final File file = new File(rootDir, folder);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (type != FolderType.Default) {
            final VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
            if (type != FolderType.Exclude) {
                e.addSourceFolder(virtualFile, type == FolderType.Test);
            } else {
                e.addExcludeFolder(virtualFile);
            }
        } else {
            LocalFileSystem.getInstance().refresh(false);
        }
    }

    private String replacePlaceholder(String line) {
        if (line.contains("$APPID$")) {
            line = line.replace("$APPID$", IssUtils.generateAppId());
        }

        return line.replace("$APPNAME$", model.getAppName()).replace("$APPVERSION$", model.getAppVersion())
                .replace("$APPPUBLISHER$", model.getAppPublisher()).replace("$APPURL$", model.getAppPublisherUrl())
                .replace("$SETUPNAME$", model.getSetupBaseName()).replace("$SETUPOUT$", "out");
    }

    private enum FolderType {
        Default,
        Test,
        Source,
        Exclude
    }
}

package org.pcsoft.plugins.intellij.iss.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.IssIcons;
import org.pcsoft.plugins.intellij.iss.ide.build.sdk.IssSdkType;
import org.pcsoft.plugins.intellij.iss.module.wizards.IssModuleModifyWizardStep;

import javax.swing.*;

public class IssModuleType extends ModuleType<IssModuleBuilder> {
    public static final String ID = "ISS_MODULE";

    public static IssModuleType getInstance() {
        return (IssModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    protected IssModuleType() {
        super(ID);
    }

    @NotNull
    @Override
    public IssModuleBuilder createModuleBuilder() {
        return new IssModuleBuilder();
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @NotNull
    @Override
    public String getName() {
        return "Inno Setup";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getDescription() {
        return "Module for Inno Setup Install Wizard";
    }

    @NotNull
    @Override
    public Icon getNodeIcon(boolean b) {
        return IssIcons.MODULE;
    }

    @Override
    public boolean isValidSdk(@NotNull Module module, @Nullable Sdk projectSdk) {
        return projectSdk != null && projectSdk.getSdkType().getName().equals(IssSdkType.NAME);
    }

    @Override
    public @Nullable ModuleWizardStep modifySettingsStep(@NotNull SettingsStep settingsStep, @NotNull ModuleBuilder moduleBuilder) {

        return new IssModuleModifyWizardStep();
    }
}

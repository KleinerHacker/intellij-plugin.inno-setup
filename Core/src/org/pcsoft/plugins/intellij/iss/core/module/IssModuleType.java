package org.pcsoft.plugins.intellij.iss.core.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.IssIcons;
import org.pcsoft.plugins.intellij.iss.core.ide.build.sdk.IssSdkType;

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
}

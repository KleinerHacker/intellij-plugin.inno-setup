package org.pcsoft.plugins.intellij.iss.module;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.IssIcons;

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
        return IssIcons.FILE;
    }
}

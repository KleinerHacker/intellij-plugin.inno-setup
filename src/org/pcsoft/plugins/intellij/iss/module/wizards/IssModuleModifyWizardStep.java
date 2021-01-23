package org.pcsoft.plugins.intellij.iss.module.wizards;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.roots.ui.configuration.SdkComboBoxBase;
import com.intellij.openapi.roots.ui.configuration.SdkListModel;
import com.intellij.openapi.roots.ui.configuration.SdkListModelBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class IssModuleModifyWizardStep extends ModuleWizardStep {
    public IssModuleModifyWizardStep() {
    }

    @Override
    public JComponent getComponent() {
        return null;
    }

    @Override
    public void updateDataModel() {

    }

    private static class IssSdkComboBox extends SdkComboBoxBase {

        protected IssSdkComboBox(@NotNull SdkListModelBuilder model) {
            super(model);
        }

        @Override
        protected void onModelUpdated(@NotNull SdkListModel sdkListModel) {

        }
    }
}

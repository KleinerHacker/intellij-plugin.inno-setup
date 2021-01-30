package org.pcsoft.plugins.intellij.iss.module;

import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ui.configuration.*;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.panels.VerticalBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.ide.build.sdk.IssSdkType;

import javax.swing.*;
import java.awt.*;

public class IssModuleConfigurationEditorProvider implements ModuleConfigurationEditorProvider {
    @Override
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState moduleConfigurationState) {
        return new ModuleConfigurationEditor[] {
                new ModuleConfigurationEditor() {
                    private final JPanel pnlRoot = new JPanel(new BorderLayout());

                    {
                        final Project project = ProjectManager.getInstance().getOpenProjects()[0];

                        final VerticalBox verticalBox = new VerticalBox();
                        {
                            final LabeledComponent<IssSdkComboBox> lblSdkBox = new LabeledComponent<>();
                            {
                                final ProjectSdksModel projectSdksModel = new ProjectSdksModel();
                                projectSdksModel.reset(project);
                                final IssSdkComboBox cmbSdkBox = new IssSdkComboBox(new SdkListModelBuilder(project, projectSdksModel, sdkTypeId -> sdkTypeId.getName().equals(IssSdkType.NAME), null, sdk -> sdk.getSdkType().getName().equals(IssSdkType.NAME)));
                                lblSdkBox.setComponent(cmbSdkBox);
                            }
                            verticalBox.add(lblSdkBox);
                        }

                        pnlRoot.add(verticalBox, BorderLayout.NORTH);
                    }

                    @Override
                    public @NlsContexts.ConfigurableName String getDisplayName() {
                        return "Inno Setup Module";
                    }

                    @Override
                    public @Nullable JComponent createComponent() {
                        return pnlRoot;
                    }

                    @Override
                    public boolean isModified() {
                        return false;
                    }

                    @Override
                    public void apply() throws ConfigurationException {

                    }
                }
        };
    }

    private static class IssSdkComboBox extends SdkComboBoxBase<IssSdkType> {

        protected IssSdkComboBox(@NotNull SdkListModelBuilder model) {
            super(model);
        }

        @Override
        protected void onModelUpdated(@NotNull SdkListModel sdkListModel) {

        }
    }
}

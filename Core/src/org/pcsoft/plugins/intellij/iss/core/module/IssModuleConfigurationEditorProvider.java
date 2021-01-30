package org.pcsoft.plugins.intellij.iss.core.module;

import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.LightColors;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.ui.components.panels.VerticalBox;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.ide.build.sdk.IssSdkType;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class IssModuleConfigurationEditorProvider implements ModuleConfigurationEditorProvider {
    @Override
    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState moduleConfigurationState) {
        return new ModuleConfigurationEditor[]{
                new ModuleConfigurationEditor() {
                    private final JPanel pnlRoot = new JPanel(new BorderLayout());
                    private final IssSdkComboBox cmbSdk;

                    private boolean isChanged = false;

                    {
                        final Project project = ProjectManager.getInstance().getOpenProjects()[0];

                        final VerticalBox verticalBox = new VerticalBox();
                        {
                            final LabeledComponent<IssSdkComboBox> lblSdkBox = new LabeledComponent<>();
                            {
                                final ProjectSdksModel projectSdksModel = new ProjectSdksModel();
                                projectSdksModel.reset(project);
                                cmbSdk = new IssSdkComboBox(projectSdksModel);
                                cmbSdk.setSelectedItem(moduleConfigurationState.getRootModel().getSdk());
                                cmbSdk.addItemListener(e -> isChanged = true);
                                lblSdkBox.setComponent(cmbSdk);
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
                    public @Nullable
                    JComponent createComponent() {
                        return pnlRoot;
                    }

                    @Override
                    public boolean isModified() {
                        return isChanged;
                    }

                    @Override
                    public void apply() throws ConfigurationException {
                        if (cmbSdk.getSelectedIndex() >= 0) {
                            moduleConfigurationState.getRootModel().setSdk(cmbSdk.getItem());
                        }
                        isChanged = false;
                    }

                    @Override
                    public JComponent getPreferredFocusedComponent() {
                        return cmbSdk;
                    }
                }
        };
    }

    private static class IssSdkComboBox extends ComboBox<Sdk> {

        protected IssSdkComboBox(ProjectSdksModel model) {
            super(new DefaultComboBoxModel<>(
                    Arrays.stream(model.getSdks())
                            .filter(x -> x.getSdkType().getName().equals(IssSdkType.NAME))
                            .toArray(Sdk[]::new)
            ));
            setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                if (value == null)
                    return new JLabel("No SDK selected");

                final HorizontalBox hBox = new HorizontalBox();
                {
                    hBox.add(new JLabel(((SdkType) value.getSdkType()).getIcon()));
                    hBox.add(new JLabel(" " + value.getName()));
                    final JLabel label = new JLabel(" (" + value.getVersionString() + ")");
                    label.setForeground(LightColors.SLIGHTLY_GRAY);
                    hBox.add(label);
                }

                return hBox;
            });
        }
    }
}

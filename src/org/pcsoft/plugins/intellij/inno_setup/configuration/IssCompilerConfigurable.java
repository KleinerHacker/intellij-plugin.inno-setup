package org.pcsoft.plugins.intellij.inno_setup.configuration;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

/**
 * Created by Christoph on 17.11.2015.
 */
public class IssCompilerConfigurable extends BaseConfigurable {
    private final BorderLayoutPanel pnlSettings;
    private final TextFieldWithBrowseButton txtInstallationPlace;

    private final IssCompilerSettings settings = ServiceManager.getService(IssCompilerSettings.class);

    public IssCompilerConfigurable() {
        txtInstallationPlace = new TextFieldWithBrowseButton();
        txtInstallationPlace.setEditable(false);
        txtInstallationPlace.addActionListener(e -> {
            txtInstallationPlace.setText("Test");

            myModified = true;

            final VirtualFile vFile = FileChooser.chooseFile(new FileChooserDescriptor(false, true, false, false, false, false), ProjectCoreUtil.theProject, null);
            if (vFile != null) {
                if (!new File(vFile.getPath(), "ISCC.exe").exists()) {
                    JOptionPane.showMessageDialog(null, "Unable to find ISCC.exe in target directory!");
                    return;
                }

                final String file = vFile.getPath();
                txtInstallationPlace.setText(file);

                myModified = true;
            }
        });

        final LabeledComponent<TextFieldWithBrowseButton> pnlCompiler = new LabeledComponent<>();
        pnlCompiler.setComponent(txtInstallationPlace);

        pnlSettings = new BorderLayoutPanel();
        pnlSettings.addToTop(pnlCompiler);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Inno Setup Compiler";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "Setup the Inno Setup Installation to use the Inno Setup Compiler in IDE.";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return pnlSettings;
    }

    @Override
    public void apply() throws ConfigurationException {
        settings.setInstallationPlace(txtInstallationPlace.getText());

        myModified = false;
    }

    @Override
    public void reset() {
        txtInstallationPlace.setText(settings.getInstallationPlace() == null ? "" : settings.getInstallationPlace());

        myModified = false;
    }

    @Override
    public void disposeUIResources() {
        txtInstallationPlace.dispose();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return txtInstallationPlace;
    }
}

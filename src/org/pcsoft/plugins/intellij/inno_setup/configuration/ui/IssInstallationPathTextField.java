package org.pcsoft.plugins.intellij.inno_setup.configuration.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.io.File;
import java.util.function.Consumer;

/**
 * Created by Christoph on 23.11.2015.
 */
public class IssInstallationPathTextField extends LabeledComponent<TextFieldWithBrowseButton> {
    final TextFieldWithBrowseButton txtInstallationPlace = new TextFieldWithBrowseButton();

    public IssInstallationPathTextField(Consumer<Boolean> modificationCallback) {
        txtInstallationPlace.setEditable(false);
        txtInstallationPlace.addActionListener(e -> {
            final VirtualFile vFile = FileChooser.chooseFile(new FileChooserDescriptor(false, true, false, false, false, false), ProjectCoreUtil.theProject, null);
            if (vFile != null) {
                if (!new File(vFile.getPath(), "ISCC.exe").exists()) {
                    JOptionPane.showMessageDialog(null, "Unable to find ISCC.exe in target directory!");
                    return;
                }

                final String file = vFile.getPath();
                txtInstallationPlace.setText(file);

                modificationCallback.accept(true);
            }
        });

        setText("Inno Setup Installation Path:");
        setComponent(txtInstallationPlace);
    }

    public void setValue(String text) {
        txtInstallationPlace.setText(text);
    }

    public String getValue() {
        return txtInstallationPlace.getText();
    }
}

package org.pcsoft.plugins.intellij.inno_setup.configuration.app.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.JTextField;
import java.util.function.Consumer;

/**
 * Created by Christoph on 23.11.2015.
 */
public class IssInstallationPathTextField extends LabeledComponent<TextFieldWithBrowseButton> {
    final TextFieldWithBrowseButton txtInstallationPlace = new TextFieldWithBrowseButton();

    public IssInstallationPathTextField(Consumer<Boolean> modificationCallback) {
        txtInstallationPlace.setEditable(false);
        txtInstallationPlace.addBrowseFolderListener("Select Inno Setup Home Path", "Select the inno setup home path with ISCC.exe inside.", ProjectCoreUtil.theProject,
                new FileChooserDescriptor(false, true, false, false, false, false), new TextComponentAccessor<JTextField>() {
                    @Override
                    public String getText(JTextField jTextField) {
                        return jTextField.getText();
                    }

                    @Override
                    public void setText(JTextField jTextField, String s) {
                        jTextField.setText(s);
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

package org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssOutputBaseFilenameTextField extends LabeledComponent<TextFieldWithBrowseButton> {
    private final TextFieldWithBrowseButton txtBaseFilename;

    public IssOutputBaseFilenameTextField() {
        txtBaseFilename = new TextFieldWithBrowseButton();
        txtBaseFilename.getButton().setIcon(AllIcons.Actions.Reset_to_default);
        txtBaseFilename.addActionListener(e -> txtBaseFilename.setText(null));

        setText("Base Output Filename (without extension):");
        setComponent(txtBaseFilename);
    }

    public String getValue() {
        return txtBaseFilename.getText();
    }

    public void setValue(String text) {
        txtBaseFilename.setText(text);
    }
}

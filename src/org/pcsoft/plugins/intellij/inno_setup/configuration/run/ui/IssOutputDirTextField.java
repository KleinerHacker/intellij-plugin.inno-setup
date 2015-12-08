package org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.ui.FixedSizeButton;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.panels.HorizontalBox;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssOutputDirTextField extends LabeledComponent<HorizontalBox> {
    private final HorizontalBox pnl;
    private final TextFieldWithBrowseButton txtOutputDir;
    private final FixedSizeButton btnReset;

    public IssOutputDirTextField() {
        txtOutputDir = new TextFieldWithBrowseButton();
        txtOutputDir.setEditable(false);
        txtOutputDir.addBrowseFolderListener("Select Output Folder", "Select an output folder for the Inno Setup result.", ProjectCoreUtil.theProject,
                new FileChooserDescriptor(false, true, false, false, false, false));

        btnReset = new FixedSizeButton(txtOutputDir);
        btnReset.setIcon(AllIcons.Actions.Reset_to_default);
        btnReset.addActionListener(e -> txtOutputDir.setText(null));

        pnl = new HorizontalBox();
        pnl.add(txtOutputDir);
        pnl.add(btnReset);

        setText("Optional Output Directory:");
        setComponent(pnl);
    }

    public String getValue() {
        return txtOutputDir.getText();
    }

    public void setValue(String text) {
        txtOutputDir.setText(text);
    }
}

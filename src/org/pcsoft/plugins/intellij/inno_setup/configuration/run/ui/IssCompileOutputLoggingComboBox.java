package org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import org.pcsoft.plugins.intellij.inno_setup.configuration.run.IssCompileRunOutputLogging;

import javax.swing.DefaultComboBoxModel;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssCompileOutputLoggingComboBox extends LabeledComponent<ComboBox> {
    private final ComboBox cmbOutputLogging;

    public IssCompileOutputLoggingComboBox() {
        cmbOutputLogging = new ComboBox(new DefaultComboBoxModel<>(IssCompileRunOutputLogging.values()));
        cmbOutputLogging.setSelectedItem(IssCompileRunOutputLogging.getDefault());

        setText("Compile Output Logging:");
        setComponent(cmbOutputLogging);
    }

    public IssCompileRunOutputLogging getValue() {
        return (IssCompileRunOutputLogging) cmbOutputLogging.getSelectedItem();
    }

    public void setValue(IssCompileRunOutputLogging anObject) {
        if (anObject == null) {
            cmbOutputLogging.setSelectedItem(IssCompileRunOutputLogging.getDefault());
            return;
        }

        cmbOutputLogging.setSelectedItem(anObject);
    }
}

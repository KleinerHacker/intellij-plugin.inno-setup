package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssInstallationPlace;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssRunSettingsEditor extends SettingsEditor<IssRunConfiguration> {
    private final ComboBox cmbInnoSetupInstallation = new ComboBox();

    public IssRunSettingsEditor() {
        cmbInnoSetupInstallation.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null)
                    return new JLabel();

                return new JLabel(((IssInstallationPlace)value).getName());
            }
        });
    }

    @Override
    protected void resetEditorFrom(IssRunConfiguration issRunConfiguration) {
        final DefaultComboBoxModel<IssInstallationPlace> model = new DefaultComboBoxModel<>();
        model.addElement(new IssInstallationPlace("Sample 1", new File("C:/")));
        model.addElement(new IssInstallationPlace("Sample 2", new File("C:/")));
        model.addElement(new IssInstallationPlace("Sample 3", new File("C:/")));

        cmbInnoSetupInstallation.setModel(model);
        cmbInnoSetupInstallation.setSelectedItem(issRunConfiguration.getInstallationPlace());
    }

    @Override
    protected void applyEditorTo(IssRunConfiguration issRunConfiguration) throws ConfigurationException {
        issRunConfiguration.setInstallationPlace((IssInstallationPlace) cmbInnoSetupInstallation.getSelectedItem());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        final LabeledComponent<ComponentWithBrowseButton<ComboBox>> labeledComponent = new LabeledComponent<>();
        labeledComponent.setText("Inno Setup Installation:");
        labeledComponent.setComponent(new ComponentWithBrowseButton<>(cmbInnoSetupInstallation, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final DialogBuilder dialogBuilder = new DialogBuilder();
                dialogBuilder.setTitle("Inno Setup Installation Manager");
                dialogBuilder.addOkAction();
                dialogBuilder.addCancelAction();
                dialogBuilder.setCenterPanel(new JBList());

                dialogBuilder.show();
            }
        }));
        return labeledComponent;
    }
}

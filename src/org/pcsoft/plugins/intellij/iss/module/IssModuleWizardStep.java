package org.pcsoft.plugins.intellij.iss.module;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.VerticalBox;
import org.pcsoft.plugins.intellij.iss.module.model.IssModuleModel;
import org.pcsoft.plugins.intellij.iss.util.IssUiUtils;

import javax.swing.*;
import java.awt.*;

public class IssModuleWizardStep extends ModuleWizardStep {
    private final JPanel pnlRoot;
    private final JBCheckBox ckbCreateScript;
    private final JBTextField txtFilename;
    private final JBTextField txtAppName, txtAppVersion;
    private final JBTextField txtAppPublisher, txtAppPublisherUrl;
    private final JBTextField txtBaseSetupName;

    private final IssModuleModel model;

    public IssModuleWizardStep(IssModuleModel model) {
        this.model = model;

        final VerticalBox pnlMain = new VerticalBox();
        {
            ckbCreateScript = new JBCheckBox("Create Script");
            txtFilename = new JBTextField();
            txtAppName = new JBTextField();
            txtAppVersion = new JBTextField();
            txtAppPublisher = new JBTextField();
            txtAppPublisherUrl = new JBTextField();
            txtBaseSetupName = new JBTextField();

            ckbCreateScript.setSelected(true);
            ckbCreateScript.addActionListener(e -> {
                txtFilename.setEnabled(ckbCreateScript.isSelected());
                txtAppName.setEnabled(ckbCreateScript.isSelected());
                txtAppVersion.setEnabled(ckbCreateScript.isSelected());
                txtAppPublisher.setEnabled(ckbCreateScript.isSelected());
                txtAppPublisherUrl.setEnabled(ckbCreateScript.isSelected());
                txtBaseSetupName.setEnabled(ckbCreateScript.isSelected());
            });
            pnlMain.add(ckbCreateScript);
            pnlMain.add(new JSeparator(JSeparator.HORIZONTAL));
            pnlMain.add(IssUiUtils.createLabeledComponent("Filename:", txtFilename));
            pnlMain.add(IssUiUtils.createHorizontalBox(
                    IssUiUtils.createLabeledComponent("App Name:", txtAppName),
                    IssUiUtils.createLabeledComponent("App Version:", txtAppVersion)
            ));
            pnlMain.add(IssUiUtils.createHorizontalBox(
                    IssUiUtils.createLabeledComponent("App Publisher:", txtAppPublisher),
                    IssUiUtils.createLabeledComponent("App Publisher Url:", txtAppPublisherUrl)
            ));
            pnlMain.add(IssUiUtils.createLabeledComponent("Setup Base Name:", txtBaseSetupName));
        }

        pnlRoot = new JPanel(new BorderLayout());
        pnlRoot.add(pnlMain, BorderLayout.NORTH);
    }

    @Override
    public JComponent getComponent() {
        return pnlRoot;
    }

    @Override
    public void updateDataModel() {
        model.setCreateDefaultScript(ckbCreateScript.isSelected());
        model.setFilename(txtFilename.getText());
        model.setAppName(txtAppName.getText());
        model.setAppVersion(txtAppVersion.getText());
        model.setAppPublisher(txtAppPublisher.getText());
        model.setAppPublisherUrl(txtAppPublisherUrl.getText());
        model.setSetupBaseName(txtBaseSetupName.getText());
    }
}

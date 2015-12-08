package org.pcsoft.plugins.intellij.inno_setup.configuration.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.ui.components.panels.VerticalBox;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui.IssCompileOutputLoggingComboBox;
import org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui.IssOutputBaseFilenameTextField;
import org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui.IssOutputDirTextField;
import org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui.IssScriptFileTextField;
import org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui.IssSymbolList;

import javax.swing.JComponent;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssCompileRunSettingsEditor extends SettingsEditor<IssCompileRunConfiguration> {
    private final VerticalBox pnlSettings;
    private final IssScriptFileTextField txtScriptFile;
    private final IssOutputDirTextField txtOutputDir;
    private final IssOutputBaseFilenameTextField txtBaseFilename;
    private final IssCompileOutputLoggingComboBox cmbOutputLogging;
    private final IssSymbolList lstSymbol;

    public IssCompileRunSettingsEditor() {
        pnlSettings = new VerticalBox();

        txtScriptFile = new IssScriptFileTextField();
        txtOutputDir = new IssOutputDirTextField();
        txtBaseFilename = new IssOutputBaseFilenameTextField();
        cmbOutputLogging = new IssCompileOutputLoggingComboBox();
        lstSymbol = new IssSymbolList();
        pnlSettings.add(txtScriptFile);
        pnlSettings.add(txtOutputDir);
        pnlSettings.add(txtBaseFilename);
        pnlSettings.add(cmbOutputLogging);
        pnlSettings.add(lstSymbol);
    }

    @Override
    protected void resetEditorFrom(IssCompileRunConfiguration issCompileRunConfiguration) {
        txtScriptFile.setValue(issCompileRunConfiguration.getScriptFile());
        txtOutputDir.setValue(issCompileRunConfiguration.getOutputDir());
        txtBaseFilename.setValue(issCompileRunConfiguration.getOutputBaseFilename());
        cmbOutputLogging.setValue(issCompileRunConfiguration.getOutputLogging());
        lstSymbol.setData(issCompileRunConfiguration.getSymbolValues());
    }

    @Override
    protected void applyEditorTo(IssCompileRunConfiguration issCompileRunConfiguration) throws ConfigurationException {
        issCompileRunConfiguration.setScriptFile(txtScriptFile.getValue());
        issCompileRunConfiguration.setOutputDir(txtOutputDir.getValue());
        issCompileRunConfiguration.setOutputBaseFilename(txtBaseFilename.getValue());
        issCompileRunConfiguration.setOutputLogging(cmbOutputLogging.getValue());
        issCompileRunConfiguration.setSymbolValues(lstSymbol.getData());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return pnlSettings;
    }
}

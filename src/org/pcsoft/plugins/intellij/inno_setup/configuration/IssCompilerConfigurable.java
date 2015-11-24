package org.pcsoft.plugins.intellij.inno_setup.configuration;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.configuration.ui.IssInstallationPathTextField;
import org.pcsoft.plugins.intellij.inno_setup.configuration.ui.IssLanguageTable;
import org.pcsoft.plugins.intellij.inno_setup.configuration.ui.IssLanguagesPathTextField;

import javax.swing.*;

/**
 * Created by Christoph on 17.11.2015.
 */
public class IssCompilerConfigurable extends BaseConfigurable {
    private final BorderLayoutPanel pnlSettings = new BorderLayoutPanel();
    private final IssInstallationPathTextField txtInstallationPath;
    private final IssLanguagesPathTextField txtLanguagesPath;
    private final IssLanguageTable tblLanguages;

    private final IssCompilerSettings settings = ServiceManager.getService(IssCompilerSettings.class);

    public IssCompilerConfigurable() {
        txtInstallationPath = new IssInstallationPathTextField(b -> myModified = true);
        txtLanguagesPath = new IssLanguagesPathTextField(b -> myModified = true);
        tblLanguages = new IssLanguageTable();
        tblLanguages.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        final VerticalBox pnlBasicSettings = new VerticalBox();
        pnlBasicSettings.add(txtInstallationPath);
        pnlBasicSettings.add(txtLanguagesPath);

        pnlSettings.addToTop(pnlBasicSettings);
        pnlSettings.addToCenter(tblLanguages);
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
        settings.setInstallationPlace(txtInstallationPath.getValue());
        settings.setLanguagePlace(txtLanguagesPath.getValue());
        tblLanguages.refresh();

        myModified = false;
    }

    @Override
    public void reset() {
        txtInstallationPath.setValue(settings.getInstallationPlace() == null ? "" : settings.getInstallationPlace());
        txtLanguagesPath.setValue(settings.getLanguagePlace() == null ? IssCompilerSettings.DEFAULT_LANGUAGES_PATH : settings.getLanguagePlace());
        tblLanguages.refresh();

        myModified = false;
    }

    @Override
    public void disposeUIResources() {

    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return txtInstallationPath;
    }
}

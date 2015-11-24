package org.pcsoft.plugins.intellij.inno_setup.configuration;

import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Created by Christoph on 19.11.2015.
 */
@State(name = "SaveIssCompilerSettings", storages = @Storage(file = StoragePathMacros.APP_CONFIG + "/is_compiler.xml"))
public class IssCompilerSettings implements PersistentStateComponent<IssCompilerSettings> {
    public static final String DEFAULT_LANGUAGES_PATH = "Languages";

    private String installationPlace;
    private String languagePlace = DEFAULT_LANGUAGES_PATH;

    @Nullable
    @Override
    public IssCompilerSettings getState() {
        return this;
    }

    @Override
    public void loadState(IssCompilerSettings issCompilerSettings) {
        XmlSerializerUtil.copyBean(issCompilerSettings, this);
    }

    public String getInstallationPlace() {
        return installationPlace;
    }

    public void setInstallationPlace(String installationPlace) {
        this.installationPlace = installationPlace;
    }

    public String getLanguagePlace() {
        return languagePlace;
    }

    public void setLanguagePlace(String languagePlace) {
        this.languagePlace = languagePlace;
        if (languagePlace == null || languagePlace.trim().isEmpty()) {
            this.languagePlace = DEFAULT_LANGUAGES_PATH;
        }
    }

    public File getInstallationPath() {
        return new File(installationPlace);
    }

    public File getLanguagesPath() {
        return new File(getInstallationPath(), languagePlace);
    }

    public void validateForCompileRun() throws RuntimeConfigurationException {
        if (!getInstallationPath().exists())
            throw new RuntimeConfigurationException("No Inno Setup Installation configured!");
        if (!new File(getInstallationPath(), "ISCC.exe").exists())
            throw new RuntimeConfigurationException("Unable to find ISCC.exe in Inno Setup Installation path!");
    }

    public boolean isValid() {
        try {
            validateForCompileRun();
        } catch (RuntimeConfigurationException e) {
            return false;
        }

        if (this.languagePlace == null || this.languagePlace.trim().isEmpty() || !getLanguagesPath().exists())
            return false;

        return true;
    }
}

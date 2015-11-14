package org.pcsoft.plugins.intellij.inno_setup.configuration;

import java.io.File;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssInstallationPlace {
    private String name;
    private File innoSetupApp;

    public IssInstallationPlace() {
    }

    public IssInstallationPlace(String name, File innoSetupApp) {
        this.name = name;
        this.innoSetupApp = innoSetupApp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getInnoSetupApp() {
        return innoSetupApp;
    }

    public void setInnoSetupApp(File innoSetupApp) {
        this.innoSetupApp = innoSetupApp;
    }
}

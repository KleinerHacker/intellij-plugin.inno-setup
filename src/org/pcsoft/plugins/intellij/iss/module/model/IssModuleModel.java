package org.pcsoft.plugins.intellij.iss.module.model;

public class IssModuleModel {
    private boolean createDefaultScript;
    private String filename;
    private String appName, appVersion;
    private String appPublisher, appPublisherUrl;
    private String setupBaseName;

    public boolean isCreateDefaultScript() {
        return createDefaultScript;
    }

    public void setCreateDefaultScript(boolean createDefaultScript) {
        this.createDefaultScript = createDefaultScript;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppPublisher() {
        return appPublisher;
    }

    public void setAppPublisher(String appPublisher) {
        this.appPublisher = appPublisher;
    }

    public String getAppPublisherUrl() {
        return appPublisherUrl;
    }

    public void setAppPublisherUrl(String appPublisherUrl) {
        this.appPublisherUrl = appPublisherUrl;
    }

    public String getSetupBaseName() {
        return setupBaseName;
    }

    public void setSetupBaseName(String setupBaseName) {
        this.setupBaseName = setupBaseName;
    }
}

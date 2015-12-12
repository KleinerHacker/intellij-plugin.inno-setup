package org.pcsoft.plugins.intellij.inno_setup.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.wizard.WizardDialog;
import org.jetbrains.annotations.NotNull;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public class IssCreateScriptWizardDialog extends WizardDialog<IssCreateScriptWizardModel> {
    public static final class Result {
        private final String filename;
        private final String appName, appVersion;
        private final String appPublisher, appPublisherURL;
        private final String setupBaseFilename, setupOutputDirectory;

        private Result(IssCreateScriptWizardModel model) {
            this(model.getFilename(), model.getAppName(), model.getAppVersion(), model.getAppPublisher(), model.getAppPublisherURL(),
                    model.getSetupBaseFilename(), model.getSetupOutputDirectory());
        }

        private Result(String filename, String appName, String appVersion, String appPublisher, String appPublisherURL, String setupBaseFilename, String setupOutputDirectory) {
            this.filename = filename;
            this.appName = appName;
            this.appVersion = appVersion;
            this.appPublisher = appPublisher;
            this.appPublisherURL = appPublisherURL;
            this.setupBaseFilename = setupBaseFilename;
            this.setupOutputDirectory = setupOutputDirectory;
        }

        public String getFilename() {
            return filename;
        }

        public String getAppName() {
            return appName;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public String getAppPublisher() {
            return appPublisher;
        }

        public String getAppPublisherURL() {
            return appPublisherURL;
        }

        public String getSetupBaseFilename() {
            return setupBaseFilename;
        }

        public String getSetupOutputDirectory() {
            return setupOutputDirectory;
        }
    }

    public IssCreateScriptWizardDialog(Project project) {
        super(project, true, new IssCreateScriptWizardModel());
    }

    @NotNull
    public Result getResult() {
        return new Result(myModel);
    }
}

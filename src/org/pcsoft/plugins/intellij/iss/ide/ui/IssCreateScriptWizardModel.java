package org.pcsoft.plugins.intellij.iss.ide.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.ui.wizard.WizardModel;
import com.intellij.ui.wizard.WizardNavigationState;
import com.intellij.ui.wizard.WizardStep;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.IssIcons;

import javax.swing.*;
import java.awt.*;

public class IssCreateScriptWizardModel extends WizardModel {
    private static final String VALUE_FILENAME = "MyScript";
    private static final String VALUE_APP_NAME = "MyApp";
    private static final String VALUE_APP_VERSION = "1.0.0";
    private static final String VALUE_APP_PUBLISHER = "MyCompany";
    private static final String VALUE_APP_PUBLISHER_URL = "http://www.mycompany.com";
    private static final String VALUE_SETUP_BASE_FILENAME = "MySetup";
    private static final String VALUE_SETUP_OUTPUT_DIRECTORY = ".";

    //region Wizard Pages
    private static final class FileNameWizardStep extends WizardStep<IssCreateScriptWizardModel> {
        private final JTextField txtName = new JTextField();
        private final JPanel pnlPage = new JPanel(new BorderLayout());

        public FileNameWizardStep() {
            super("Script File Name", "Enter a script file name for file to create without extension", IssIcons.FILE_BIG);
            initUI();
        }

        private void initUI() {
            final VerticalBox pnl = new VerticalBox();
            {
                txtName.setText(VALUE_FILENAME);

                //region Labeled Component
                final LabeledComponent<JTextField> pnlName = new LabeledComponent<>();
                pnlName.setText("Enter filename (without extension):");
                pnlName.setComponent(txtName);
                //endregion

                pnl.add(pnlName);
            }
            pnlPage.setPreferredSize(new Dimension(600, 480));
            pnlPage.add(pnl, BorderLayout.NORTH);
        }

        @Override
        public JComponent prepare(WizardNavigationState wizardNavigationState) {
            return pnlPage;
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return txtName;
        }

        @Override
        public WizardStep onPrevious(IssCreateScriptWizardModel model) {
            txtName.setText(model.filename);

            return super.onPrevious(model);
        }

        @Override
        public WizardStep onNext(IssCreateScriptWizardModel model) {
            model.filename = txtName.getText();

            return super.onNext(model);
        }
    }

    private static final class UserDataWizardStep extends WizardStep<IssCreateScriptWizardModel> {
        private final JTextField txtAppName = new JTextField();
        private final JTextField txtAppVersion = new JTextField();
        private final JTextField txtAppPublisher = new JTextField();
        private final JTextField txtAppPublisherUrl = new JTextField();
        private final JPanel pnlPage = new JPanel(new BorderLayout());

        public UserDataWizardStep() {
            super("Script User Data", "Enter all needed script data for the user to insert into the template.", IssIcons.FILE_BIG);
            initUI();
        }

        private void initUI() {
            final VerticalBox pnl = new VerticalBox();
            {
                txtAppName.setText(VALUE_APP_NAME);
                txtAppVersion.setText(VALUE_APP_VERSION);
                txtAppPublisher.setText(VALUE_APP_PUBLISHER);
                txtAppPublisherUrl.setText(VALUE_APP_PUBLISHER_URL);

                //region Labeled Components
                final LabeledComponent<JTextField> pnlAppName = new LabeledComponent<>();
                pnlAppName.setText("Enter Application Name:");
                pnlAppName.setComponent(txtAppName);

                final LabeledComponent<JTextField> pnlAppVersion = new LabeledComponent<>();
                pnlAppVersion.setText("Enter Application Version:");
                pnlAppVersion.setComponent(txtAppVersion);

                final LabeledComponent<JTextField> pnlAppPublisher = new LabeledComponent<>();
                pnlAppPublisher.setText("Enter Application Publisher:");
                pnlAppPublisher.setComponent(txtAppPublisher);

                final LabeledComponent<JTextField> pnlAppPublisherUrl = new LabeledComponent<>();
                pnlAppPublisherUrl.setText("Enter Application Publisher URL:");
                pnlAppPublisherUrl.setComponent(txtAppPublisherUrl);
                //endregion

                pnl.add(pnlAppName);
                pnl.add(pnlAppVersion);
                pnl.add(pnlAppPublisher);
                pnl.add(pnlAppPublisherUrl);
            }
            pnlPage.setPreferredSize(new Dimension(600, 480));
            pnlPage.add(pnl, BorderLayout.NORTH);
        }

        @Override
        public JComponent prepare(WizardNavigationState wizardNavigationState) {
            return pnlPage;
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return txtAppName;
        }

        @Override
        public WizardStep onNext(IssCreateScriptWizardModel model) {
            model.appName = txtAppName.getText();
            model.appVersion = txtAppVersion.getText();
            model.appPublisher = txtAppPublisher.getText();
            model.appPublisherURL = txtAppPublisherUrl.getText();

            return super.onNext(model);
        }

        @Override
        public WizardStep onPrevious(IssCreateScriptWizardModel model) {
            txtAppName.setText(model.appName);
            txtAppVersion.setText(model.appVersion);
            txtAppPublisher.setText(model.appPublisher);
            txtAppPublisherUrl.setText(model.appPublisherURL);

            return super.onPrevious(model);
        }
    }

    private static final class TechnicalDataWizardStep extends WizardStep<IssCreateScriptWizardModel> {
        private final JTextField txtBaseFilename = new JTextField();
        private final TextFieldWithBrowseButton txtOutputDir = new TextFieldWithBrowseButton();
        private final JPanel pnlPage = new JPanel(new BorderLayout());

        public TechnicalDataWizardStep() {
            super("Script Technical Data", "Enter all needed script data for technical details to insert into the template.", IssIcons.FILE_BIG);
            initUI();
        }

        private void initUI() {
            final VerticalBox pnl = new VerticalBox();
            {
                txtBaseFilename.setText(VALUE_SETUP_BASE_FILENAME);
                txtOutputDir.setText(VALUE_SETUP_OUTPUT_DIRECTORY);

                txtOutputDir.setEditable(false);
                txtOutputDir.addBrowseFolderListener("Select Setup Output Directory", "Select a directory for output setup file.", ProjectCoreUtil.theProject, new FileChooserDescriptor(false, true, false, false, false, false));

                //region Labeled Component
                final LabeledComponent<JTextField> pnlBaseFilename = new LabeledComponent<>();
                pnlBaseFilename.setText("Enter Setup File Name (without extension):");
                pnlBaseFilename.setComponent(txtBaseFilename);

                final LabeledComponent<TextFieldWithBrowseButton> pnlOutputDir = new LabeledComponent<>();
                pnlOutputDir.setText("Select Setup Output Directory:");
                pnlOutputDir.setComponent(txtOutputDir);
                //endregion

                pnl.add(pnlBaseFilename);
                pnl.add(pnlOutputDir);
            }

            pnlPage.setPreferredSize(new Dimension(600, 480));
            pnlPage.add(pnl, BorderLayout.NORTH);
        }

        @Override
        public JComponent prepare(WizardNavigationState wizardNavigationState) {
            return pnlPage;
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return txtBaseFilename;
        }

        @Override
        public WizardStep onNext(IssCreateScriptWizardModel model) {
            model.setupBaseFilename = txtBaseFilename.getText();
            model.setupOutputDirectory = txtOutputDir.getText();

            return super.onNext(model);
        }

        @Override
        public WizardStep onPrevious(IssCreateScriptWizardModel model) {
            txtBaseFilename.setText(model.setupBaseFilename);
            txtOutputDir.setText(model.setupOutputDirectory);

            return super.onPrevious(model);
        }
    }
    //endregion

    private String filename = VALUE_FILENAME;
    private String appName = VALUE_APP_NAME, appVersion = VALUE_APP_VERSION;
    private String appPublisher = VALUE_APP_PUBLISHER, appPublisherURL = VALUE_APP_PUBLISHER_URL;
    private String setupBaseFilename = VALUE_SETUP_BASE_FILENAME, setupOutputDirectory = VALUE_SETUP_OUTPUT_DIRECTORY;

    public IssCreateScriptWizardModel() {
        super("Create Inno Setup Script");
        add(new FileNameWizardStep());
        add(new UserDataWizardStep());
        add(new TechnicalDataWizardStep());
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

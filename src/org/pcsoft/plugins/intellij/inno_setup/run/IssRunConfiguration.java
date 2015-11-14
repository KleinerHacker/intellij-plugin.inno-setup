package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssInstallationPlace;

import javax.swing.*;
import java.io.OutputStream;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssRunConfiguration extends RunConfigurationBase {
    private IssInstallationPlace installationPlace;

    public IssRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    public IssInstallationPlace getInstallationPlace() {
        return installationPlace;
    }

    public void setInstallationPlace(IssInstallationPlace installationPlace) {
        this.installationPlace = installationPlace;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new IssRunSettingsEditor();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (installationPlace == null)
            throw new RuntimeConfigurationException("No Inno Setup Installation Place selected");
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new CommandLineState(executionEnvironment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                JOptionPane.showMessageDialog(null, "Run with: " + (installationPlace == null ? "?" : installationPlace.getName()));

                return new ProcessHandler() {
                    @Override
                    protected void destroyProcessImpl() {

                    }

                    @Override
                    protected void detachProcessImpl() {

                    }

                    @Override
                    public boolean detachIsDefault() {
                        return false;
                    }

                    @Nullable
                    @Override
                    public OutputStream getProcessInput() {
                        return null;
                    }
                };
            }
        };
    }
}

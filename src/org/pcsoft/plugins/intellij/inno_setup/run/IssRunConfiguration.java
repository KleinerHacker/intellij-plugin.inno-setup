package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectCoreUtil;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssCompilerSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Executors;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssRunConfiguration extends RunConfigurationBase {
    private String scriptFile;

    private final IssCompilerSettings settings = ServiceManager.getService(IssCompilerSettings.class);

    public IssRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new IssRunSettingsEditor();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (scriptFile == null || scriptFile.isEmpty())
            throw new RuntimeConfigurationException("No script file is set!");
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return (executor1, programRunner) -> {
            final ConsoleView consoleView = new ConsoleViewImpl(ProjectCoreUtil.theProject, true);
            try {
                final String command = "\"" + settings.getInstallationPlace() + "/ISCC.exe\" \"" + scriptFile + "\"";
                consoleView.print(command + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.SYSTEM_OUTPUT);
                final Process process = Runtime.getRuntime().exec(command);

                Executors.newCachedThreadPool().submit(() -> {
                    try {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            consoleView.print(line + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.NORMAL_OUTPUT);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
                Executors.newCachedThreadPool().submit(() -> {
                    try {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            consoleView.print(line, ConsoleViewContentType.ERROR_OUTPUT);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });

                return new DefaultExecutionResult(consoleView, new ProcessHandler() {
                    @Override
                    protected void destroyProcessImpl() {
                        process.destroy();
                    }

                    @Override
                    protected void detachProcessImpl() {
                        process.destroy();
                    }

                    @Override
                    public boolean detachIsDefault() {
                        return false;
                    }

                    @Nullable
                    @Override
                    public OutputStream getProcessInput() {
                        return process.getOutputStream();
                    }

                    @Override
                    public boolean isProcessTerminated() {
                        return !process.isAlive();
                    }

                    @Override
                    protected void notifyProcessTerminated(int exitCode) {
                        consoleView.print("Inno Setup finished with exit code " + exitCode + SystemUtils.LINE_SEPARATOR,
                                ConsoleViewContentType.SYSTEM_OUTPUT);
                        super.notifyProcessTerminated(exitCode);
                    }

                    @Override
                    protected void notifyProcessDetached() {
                        consoleView.print("Inno Setup was detached" + SystemUtils.LINE_SEPARATOR,
                                ConsoleViewContentType.SYSTEM_OUTPUT);
                        super.notifyProcessDetached();
                    }
                });
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
        };
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }
}

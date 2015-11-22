package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.ServiceManager;
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
 * Created by Christoph on 20.11.2015.
 */
public class IssRun implements RunProfileState {
    private final ConsoleView consoleView = new ConsoleViewImpl(ProjectCoreUtil.theProject, true);
    private final IssCompilerSettings compilerSettings = ServiceManager.getService(IssCompilerSettings.class);
    private final IssRunConfiguration runConfiguration;

    public IssRun(IssRunConfiguration runConfiguration) {
        consoleView.addMessageFilter((s, i) -> {
            if (s.toLowerCase().startsWith("run:") || s.startsWith("Inno Setup finished with exit code") || s.equals("Inno Setup was detached"))
                return new Filter.Result(0, s.length(), null, IssRunHighlightingColorFactory.CONSOLE_IDE.getDefaultAttributes());

            return null;
        });

        this.runConfiguration = runConfiguration;
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException {
        try {
            final String command = "\"" + compilerSettings.getInstallationPlace() + "/ISCC.exe\" \"" + runConfiguration.getScriptFile() + "\"";
            consoleView.print("Run: " + command + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.SYSTEM_OUTPUT);
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
                        consoleView.print(line + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.ERROR_OUTPUT);
                        IssRunErrorHandler.handleError(runConfiguration.getProject(), line);
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
    }
}

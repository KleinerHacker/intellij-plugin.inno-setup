package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssCompilerSettings;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Christoph on 20.11.2015.
 */
public class IssRun implements RunProfileState {
    private final IssRunConsoleView consoleView;
    private final IssCompilerSettings compilerSettings = ServiceManager.getService(IssCompilerSettings.class);
    private final IssRunConfiguration runConfiguration;

    public IssRun(IssRunConfiguration runConfiguration) {
        this.runConfiguration = runConfiguration;
        this.consoleView = new IssRunConsoleView(runConfiguration.getProject(), this::handleStandardOut, this::handleErrorOut);
    }

    @Nullable
    @Override
    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException {
        try {
            final String command = "\"" + compilerSettings.getInstallationPlace() + "/ISCC.exe\" \"" + runConfiguration.getScriptFile() + "\"";
            consoleView.printCommandLine(command);
            final Process process = Runtime.getRuntime().exec(command);

            this.consoleView.attachProcess(process);

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
                    consoleView.printTermination(exitCode);
                    super.notifyProcessTerminated(exitCode);
                }

                @Override
                protected void notifyProcessDetached() {
                    consoleView.printDetach();
                    super.notifyProcessDetached();
                }
            });
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

    private void handleStandardOut(final String line) {

    }

    private void handleErrorOut(final String line) {
        IssRunErrorHandler.handleError(runConfiguration.getProject(), line);
    }
}

package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfoBase;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.awt.RelativePoint;
import org.apache.commons.lang.SystemUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by pfeifchr on 25.11.2015.
 */
public class IssRunConsoleView extends ConsoleViewImpl {
    private final Consumer<String> standardOutCallback, errorOutCallback;

    public IssRunConsoleView(Project project, Consumer<String> standardOutCallback, Consumer<String> errorOutCallback) {
        super(project, true);
        this.standardOutCallback = standardOutCallback;
        this.errorOutCallback = errorOutCallback;

        addMessageFilter((line, offsetWithLine) -> {
            final int startOffset = offsetWithLine - line.length();
            final int endOffset = offsetWithLine;

            if (line.toLowerCase().startsWith("run:") || line.startsWith("Inno Setup finished with exit code") || line.equals("Inno Setup was detached")) {
                return new Filter.Result(startOffset, endOffset, null, IssRunHighlightingColorFactory.CONSOLE_OUT_IDE.getDefaultAttributes());
            } else if (IssRunErrorHandler.hasError(line.trim(), IssRunErrorHandler.ErrorType.CodeLine)) {
                return new Filter.Result(startOffset + line.indexOf(" in ") + 4, startOffset + line.lastIndexOf(":"), new HyperlinkInfoBase() {
                    @Override
                    public void navigate(Project project, RelativePoint relativePoint) {
                        IssRunErrorHandler.handleError(project, line.trim());
                    }
                });
            }

            return null;
        });
    }

    public void attachProcess(Process process) {
        Executors.newCachedThreadPool().submit(() -> {
            try {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    this.print(line + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.NORMAL_OUTPUT);
                    standardOutCallback.accept(line);
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
                    this.print(line + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.ERROR_OUTPUT);
                    errorOutCallback.accept(line);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void printCommandLine(final String commandLine) {
        this.print("Run: " + commandLine + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.SYSTEM_OUTPUT);
    }

    public void printTermination(final int exitCode) {
        this.print("Inno Setup finished with exit code " + exitCode + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.SYSTEM_OUTPUT);
    }

    public void printDetach() {
        this.print("Inno Setup was detached" + SystemUtils.LINE_SEPARATOR, ConsoleViewContentType.SYSTEM_OUTPUT);
    }
}

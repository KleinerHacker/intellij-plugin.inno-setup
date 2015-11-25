package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.pcsoft.plugins.intellij.inno_setup.utils.IssEditorUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph on 22.11.2015.
 */
public final class IssRunErrorHandler {
    public static enum ErrorType {
        Simple,
        CodeLine
    }

    public static boolean hasError(String line, ErrorType type) {
        switch (type) {
            case Simple:
                return line.toLowerCase().contains("error");
            case CodeLine:
                return hasError(line, ErrorType.Simple) && line.toLowerCase().contains("line");
            default:
                throw new RuntimeException();
        }
    }

    public static void handleError(Project project, String line) {
        if (hasError(line, ErrorType.CodeLine)) {
            handleCodeLineError(project, line);
        }
    }

    private static void handleCodeLineError(Project project, String line) {
        final Pattern pattern = Pattern.compile(".+line\\s([0-9]+)\\sin\\s(.+):.+");
        final Matcher matcher = pattern.matcher(line);
        if (!matcher.matches()) {
            Logger.getInstance(IssRunErrorHandler.class).warn("Unable to parse error line");
            return;
        }
        final int lineNumber = Integer.valueOf(matcher.group(1));
        final String file = matcher.group(2);

        IssEditorUtils.openFileAndGoto(project, file, lineNumber - 1);
    }

    private IssRunErrorHandler() {
    }
}

package org.pcsoft.plugins.intellij.inno_setup.configuration.run;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public enum IssCompileRunOutputLogging {
    None("No output"),
    Progress("Progress output only"),
    Verbose("Verbose output"),
    ;

    public static IssCompileRunOutputLogging getDefault() {
        return Progress;
    }

    private final String uiText;

    private IssCompileRunOutputLogging(String uiText) {
        this.uiText = uiText;
    }

    public String getUiText() {
        return uiText;
    }

    @Override
    public String toString() {
        return uiText;
    }
}

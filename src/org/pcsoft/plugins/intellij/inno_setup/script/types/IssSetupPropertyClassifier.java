package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 21.11.2015.
 */
public enum IssSetupPropertyClassifier {
    Compiler("Compiler-related"),
    Installer("Installer-related"),
    Cosmetic("Cosmetic"),
    Obsolete("Obsolete")
    ;

    private final String text;

    private IssSetupPropertyClassifier(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}

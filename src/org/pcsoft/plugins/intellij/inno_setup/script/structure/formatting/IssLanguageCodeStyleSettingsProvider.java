package org.pcsoft.plugins.intellij.inno_setup.script.structure.formatting;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        switch (settingsType) {
            case SPACING_SETTINGS:
//                consumer.showCustomOption(IssCodeStyleSettings.class, IssCodeStyleSettings.KEY_SPACES_SETUP_ASSIGNER,
//                        "Assigner", "Setup");
//                consumer.showCustomOption(IssCodeStyleSettings.class, IssCodeStyleSettings.KEY_SPACES_PROPERTY_ASSIGNER,
//                        "Assigner", "Property");
//                consumer.showCustomOption(IssCodeStyleSettings.class, IssCodeStyleSettings.KEY_SPACES_PROPERTY_TERMINATOR,
//                        "Terminator", "Property");
                consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS");
                consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Assigner");
                break;
            case BLANK_LINES_SETTINGS:
                consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");
                break;
        }
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return IssLanguage.INSTANCE;
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return "#define mySymbol abc\n\n"+
                "[Setup]\n"+
                "AppName = {#mySombol}\n" +
                "AppVersion = 1.2.3\n\n" +
                "[Task]\n" +
                "Name: myTask; Description: \"My Task\";\n" +
                "Name: yourTask; Description: \"Your Task\";\n";
    }
}

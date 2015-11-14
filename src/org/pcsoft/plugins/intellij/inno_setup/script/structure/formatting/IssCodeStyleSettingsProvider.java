package org.pcsoft.plugins.intellij.inno_setup.script.structure.formatting;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.lang.Language;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
    @Nullable
    @Override
    public String getConfigurableDisplayName() {
        return "Inno Setup Script";
    }

    @Nullable
    @Override
    public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
        return new IssCodeStyleSettings(settings);
    }

    @Nullable
    @Override
    public Language getLanguage() {
        return IssLanguage.INSTANCE;
    }

    @NotNull
    @Override
    public Configurable createSettingsPage(CodeStyleSettings codeStyleSettings, CodeStyleSettings originalSettings) {
        return new CodeStyleAbstractConfigurable(codeStyleSettings, originalSettings, "Main Settings") {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings codeStyleSettings) {
                return new IssCodeStyleMainPanel(getCurrentSettings(), codeStyleSettings);
            }

            @Nullable
            @Override
            public String getHelpTopic() {
                return null;
            }
        };
    }

    private static final class IssCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
        public IssCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
            super(IssLanguage.INSTANCE, currentSettings, settings);
        }
    }
}

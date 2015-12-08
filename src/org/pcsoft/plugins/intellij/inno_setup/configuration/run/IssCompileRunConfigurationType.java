package org.pcsoft.plugins.intellij.inno_setup.configuration.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;

import javax.swing.Icon;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssCompileRunConfigurationType extends ConfigurationTypeBase {
    private IssCompileRunConfigurationFactory configurationFactory = new IssCompileRunConfigurationFactory(this);

    public IssCompileRunConfigurationType() {
        this("INNO_SETUP_COMPILATION", "Inno Setup Compilation", "Compile an Inno Setup Script", IssIcons.IS_CMD);
    }

    public IssCompileRunConfigurationType(@NotNull String id, String displayName, String description, Icon icon) {
        super(id, displayName, description, icon);
        addFactory(configurationFactory);
    }
}

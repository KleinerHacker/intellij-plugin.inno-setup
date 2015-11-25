package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;

import javax.swing.Icon;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssRunConfigurationType extends ConfigurationTypeBase {
    private IssRunConfigurationFactory configurationFactory = new IssRunConfigurationFactory(this);

    public IssRunConfigurationType() {
        this("INNO_SETUP_COMPILATION", "Inno Setup Compilation", "Compile an Inno Setup Script", IssIcons.IS_CMD);
    }

    public IssRunConfigurationType(@NotNull String id, String displayName, String description, Icon icon) {
        super(id, displayName, description, icon);
        addFactory(configurationFactory);
    }
}

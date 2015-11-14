package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;

import javax.swing.*;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssRunConfigurationType implements ConfigurationType {

    @Override
    public String getDisplayName() {
        return "Inno Setup Compilation";
    }

    @Override
    public String getConfigurationTypeDescription() {
        return "Compile an Inno Setup Script";
    }

    @Override
    public Icon getIcon() {
        return IssIcons.IS_CMD;
    }

    @NotNull
    @Override
    public String getId() {
        return "INNO_SETUP_COMPILATION";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new IssRunConfigurationFactory(this)};
    }
}

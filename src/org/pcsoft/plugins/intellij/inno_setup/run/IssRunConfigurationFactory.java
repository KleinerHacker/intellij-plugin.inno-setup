package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssRunConfigurationFactory extends ConfigurationFactory {

    public IssRunConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new IssRunConfiguration(project, this, "Inno Setup Compilation");
    }
}

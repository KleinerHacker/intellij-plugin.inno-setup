package org.pcsoft.plugins.intellij.inno_setup.configuration.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssCompileRunConfigurationFactory extends ConfigurationFactory {

    public IssCompileRunConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new IssCompileRunConfiguration(project, this, "Inno Setup Compilation");
    }
}

package org.pcsoft.plugins.intellij.inno_setup.configuration.run;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;

/**
 * Created by pfeifchr on 25.11.2015.
 */
public class IssCompileRunConfigurationProducer extends RunConfigurationProducer<IssCompileRunConfiguration> {

    public IssCompileRunConfigurationProducer() {
        super(ConfigurationTypeUtil.findConfigurationType(IssCompileRunConfigurationType.class));
    }

    @Override
    protected boolean setupConfigurationFromContext(IssCompileRunConfiguration issCompileRunConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        if (configurationContext.getProject() == null)
            return false;
        if (configurationContext.getLocation() == null || configurationContext.getLocation().getVirtualFile() == null ||
                configurationContext.getLocation().getVirtualFile().getExtension() == null ||
                !configurationContext.getLocation().getVirtualFile().getExtension().equals("iss"))
            return false;
        if (isConfigurationFromContext(issCompileRunConfiguration, configurationContext))
            return false;

        issCompileRunConfiguration.setGeneratedName();
        issCompileRunConfiguration.setScriptFile(configurationContext.getLocation().getVirtualFile().getPath());

        return true;
    }

    @Override
    public boolean isConfigurationFromContext(IssCompileRunConfiguration issCompileRunConfiguration, ConfigurationContext configurationContext) {
        if (configurationContext.getProject() == null)
            return false;
        if (configurationContext.getLocation() == null || configurationContext.getLocation().getVirtualFile() == null ||
                configurationContext.getLocation().getVirtualFile().getExtension() == null ||
                !configurationContext.getLocation().getVirtualFile().getExtension().equals("iss"))
            return false;

        return issCompileRunConfiguration.getScriptFile().equals(configurationContext.getLocation().getVirtualFile().getPath());
    }
}

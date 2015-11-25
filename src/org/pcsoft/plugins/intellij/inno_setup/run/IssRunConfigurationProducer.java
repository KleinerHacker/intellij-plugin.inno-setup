package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;

/**
 * Created by pfeifchr on 25.11.2015.
 */
public class IssRunConfigurationProducer extends RunConfigurationProducer<IssRunConfiguration> {

    public IssRunConfigurationProducer() {
        super(ConfigurationTypeUtil.findConfigurationType(IssRunConfigurationType.class));
    }

    @Override
    protected boolean setupConfigurationFromContext(IssRunConfiguration issRunConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        if (configurationContext.getProject() == null)
            return false;
        if (configurationContext.getLocation() == null || configurationContext.getLocation().getVirtualFile() == null ||
                configurationContext.getLocation().getVirtualFile().getExtension() == null ||
                !configurationContext.getLocation().getVirtualFile().getExtension().equals("iss"))
            return false;
        if (isConfigurationFromContext(issRunConfiguration, configurationContext))
            return false;

        issRunConfiguration.setGeneratedName();
        issRunConfiguration.setScriptFile(configurationContext.getLocation().getVirtualFile().getPath());

        return true;
    }

    @Override
    public boolean isConfigurationFromContext(IssRunConfiguration issRunConfiguration, ConfigurationContext configurationContext) {
        if (configurationContext.getProject() == null)
            return false;
        if (configurationContext.getLocation() == null || configurationContext.getLocation().getVirtualFile() == null ||
                configurationContext.getLocation().getVirtualFile().getExtension() == null ||
                !configurationContext.getLocation().getVirtualFile().getExtension().equals("iss"))
            return false;

        return issRunConfiguration.getScriptFile().equals(configurationContext.getLocation().getVirtualFile().getPath());
    }
}

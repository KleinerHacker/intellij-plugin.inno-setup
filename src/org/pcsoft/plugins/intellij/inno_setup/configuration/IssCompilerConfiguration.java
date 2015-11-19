package org.pcsoft.plugins.intellij.inno_setup.configuration;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Christoph on 17.11.2015.
 */
public class IssCompilerConfiguration extends ConfigurableProvider {
    private final IssCompilerConfigurable issCompilerConfigurable = new IssCompilerConfigurable();

    @Nullable
    @Override
    public Configurable createConfigurable() {
        return issCompilerConfigurable;
    }
}

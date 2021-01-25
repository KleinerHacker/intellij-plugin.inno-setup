package org.pcsoft.plugins.intellij.iss.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IssBuilderService extends BuilderService {
    @Override
    public @NotNull List<? extends BuildTargetType<?>> getTargetTypes() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<? extends ModuleLevelBuilder> createModuleLevelBuilders() {
        return Arrays.asList(new IssModuleLevelBuilder());
    }

    @Override
    public @NotNull List<? extends TargetBuilder<?, ?>> createBuilders() {
        return Collections.emptyList();
    }
}

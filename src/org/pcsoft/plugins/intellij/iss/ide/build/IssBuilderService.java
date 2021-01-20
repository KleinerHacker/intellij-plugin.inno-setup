package org.pcsoft.plugins.intellij.iss.ide.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class IssBuilderService extends BuilderService {
    @NotNull
    @Override
    public List<? extends BuildTargetType<?>> getTargetTypes() {
        return Collections.singletonList(new IssBuildTargetType());
    }

    @NotNull
    @Override
    public List<? extends TargetBuilder<?, ?>> createBuilders() {
        return Collections.singletonList(
                new TargetBuilder<IssBuildRootDescriptor, IssBuildTarget>(Collections.singletonList(new IssBuildTargetType())) {
                    @Override
                    public void build(@NotNull IssBuildTarget issBuildTarget, @NotNull DirtyFilesHolder<IssBuildRootDescriptor, IssBuildTarget> dirtyFilesHolder, @NotNull BuildOutputConsumer buildOutputConsumer, @NotNull CompileContext compileContext) throws ProjectBuildException, IOException {

                    }

                    @NotNull
                    @Override
                    public String getPresentableName() {
                        return "Bla Blub";
                    }
                }
        );
    }
}

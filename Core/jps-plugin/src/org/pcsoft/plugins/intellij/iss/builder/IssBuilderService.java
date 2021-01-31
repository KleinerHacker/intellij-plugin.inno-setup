package org.pcsoft.plugins.intellij.iss.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.*;

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

                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @NotNull
                    @Override
                    public String getPresentableName() {
                        return "Bla Blub";
                    }
                }
        );
    }

    @Override
    public @NotNull List<? extends ModuleLevelBuilder> createModuleLevelBuilders() {
        return super.createModuleLevelBuilders();
    }
}

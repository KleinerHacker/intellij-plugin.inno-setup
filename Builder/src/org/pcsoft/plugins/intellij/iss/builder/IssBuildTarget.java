package org.pcsoft.plugins.intellij.iss.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.*;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class IssBuildTarget extends BuildTarget<IssBuildRootDescriptor> {
    public IssBuildTarget(BuildTargetType<? extends BuildTarget<IssBuildRootDescriptor>> targetType) {
        super(targetType);
    }

    @Override
    public String getId() {
        return "ISS";
    }

    @Override
    public Collection<BuildTarget<?>> computeDependencies(BuildTargetRegistry buildTargetRegistry, TargetOutputIndex targetOutputIndex) {
        return new ArrayList<>();
    }

    @NotNull
    @Override
    public List<IssBuildRootDescriptor> computeRootDescriptors(JpsModel jpsModel, ModuleExcludeIndex moduleExcludeIndex, IgnoredFileIndex ignoredFileIndex, BuildDataPaths buildDataPaths) {
        return Collections.singletonList(new IssBuildRootDescriptor());
    }

    @Nullable
    @Override
    public IssBuildRootDescriptor findRootDescriptor(String s, BuildRootIndex buildRootIndex) {
        return new IssBuildRootDescriptor();
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "Inno Setup Build Target";
    }

    @NotNull
    @Override
    public Collection<File> getOutputRoots(CompileContext compileContext) {
        return new ArrayList<>();
    }
}

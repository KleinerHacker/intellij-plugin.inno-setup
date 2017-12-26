package org.pcsoft.plugins.intellij.iss.ide.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.model.JpsModel;

import java.util.Collections;
import java.util.List;

public class IssBuildTargetType extends BuildTargetType<IssBuildTarget> {
    public IssBuildTargetType() {
        super("ISS");
    }

    @NotNull
    @Override
    public List<IssBuildTarget> computeAllTargets(@NotNull JpsModel jpsModel) {
        return Collections.singletonList(new IssBuildTarget(this));
    }

    @NotNull
    @Override
    public BuildTargetLoader<IssBuildTarget> createLoader(@NotNull JpsModel jpsModel) {
        return new IssBuildTargetLoader(this);
    }
}

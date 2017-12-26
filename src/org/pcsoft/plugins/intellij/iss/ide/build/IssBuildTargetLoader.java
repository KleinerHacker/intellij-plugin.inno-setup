package org.pcsoft.plugins.intellij.iss.ide.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTargetLoader;

public class IssBuildTargetLoader extends BuildTargetLoader<IssBuildTarget> {
    private final IssBuildTargetType buildTargetType;

    public IssBuildTargetLoader(IssBuildTargetType buildTargetType) {
        this.buildTargetType = buildTargetType;
    }

    @Nullable
    @Override
    public IssBuildTarget createTarget(@NotNull String s) {
        return new IssBuildTarget(buildTargetType);
    }
}

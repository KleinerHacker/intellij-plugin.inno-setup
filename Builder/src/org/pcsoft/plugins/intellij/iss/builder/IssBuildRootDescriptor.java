package org.pcsoft.plugins.intellij.iss.builder;

import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.BuildTarget;

import java.io.File;

public class IssBuildRootDescriptor extends BuildRootDescriptor {
    @Override
    public String getRootId() {
        return "ISS";
    }

    @Override
    public File getRootFile() {
        return null;
    }

    @Override
    public BuildTarget<?> getTarget() {
        return null;
    }
}

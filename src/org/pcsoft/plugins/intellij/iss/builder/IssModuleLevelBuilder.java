package org.pcsoft.plugins.intellij.iss.builder;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.ModuleChunk;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.builders.java.JavaSourceRootDescriptor;
import org.jetbrains.jps.incremental.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class IssModuleLevelBuilder extends ModuleLevelBuilder {
    protected IssModuleLevelBuilder() {
        super(BuilderCategory.TRANSLATOR);
    }

    @Override
    public ExitCode build(CompileContext compileContext, ModuleChunk moduleChunk, DirtyFilesHolder<JavaSourceRootDescriptor, ModuleBuildTarget> dirtyFilesHolder, OutputConsumer outputConsumer) throws ProjectBuildException, IOException {
        dirtyFilesHolder.processDirtyFiles((moduleBuildTarget, file, javaSourceRootDescriptor) -> {
            Runtime.getRuntime().exec("\"F:\\Inno Setup 5\\iscc.exe\" " + file);
            return true;
        });

        return ExitCode.OK;
    }

    @Override
    public @NotNull List<String> getCompilableFileExtensions() {
        return Arrays.asList("iss");
    }

    @Override
    public @NotNull
    @Nls(capitalization = Nls.Capitalization.Sentence) String getPresentableName() {
        return "Inno Setup Builder";
    }
}

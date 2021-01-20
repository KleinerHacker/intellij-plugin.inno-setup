package org.pcsoft.plugins.intellij.iss.ide.build.compiler;

import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.Compiler;
import org.jetbrains.annotations.NotNull;

public class IssCompiler implements Compiler {
    @NotNull
    @Override
    public String getDescription() {
        return "Inno Setup Script Compiler";
    }

    @Override
    public boolean validateConfiguration(CompileScope compileScope) {
        return true;
    }
}

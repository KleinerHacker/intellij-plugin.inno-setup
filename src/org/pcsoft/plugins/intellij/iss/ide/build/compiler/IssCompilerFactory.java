package org.pcsoft.plugins.intellij.iss.ide.build.compiler;

import com.intellij.openapi.compiler.Compiler;
import com.intellij.openapi.compiler.CompilerFactory;
import com.intellij.openapi.compiler.CompilerManager;
import org.jetbrains.annotations.NotNull;

public class IssCompilerFactory implements CompilerFactory {
    @Override
    public Compiler[] createCompilers(@NotNull CompilerManager compilerManager) {
        return new Compiler[] {new IssCompiler()};
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.types.cd;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveParameterIdentifier;

import javax.swing.Icon;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public interface IssCompilerDirectiveSectionIdentifier {
    @NotNull
    String getId();

    @Nullable
    String getDescriptionKey();

    @Nullable
    Icon getIcon();

    @NotNull
    IElementType getElementType();

    @NotNull
    Class<? extends IssCompilerDirectiveParameterIdentifier> getParameterIdentifierClass();

    @NotNull
    IssCompilerDirectiveParameterIdentifier[] getParameterIdentifiers();
}

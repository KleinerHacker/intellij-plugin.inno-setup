package org.pcsoft.plugins.intellij.inno_setup.script.types.cd;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveIncludeParameterType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveParameterIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectivePreProcessorParameterType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveSymbolDefineParameterType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveSymbolUndefineParameterType;

import javax.swing.Icon;
import java.util.stream.Stream;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public enum IssCompilerDirectiveSectionType implements IssCompilerDirectiveSectionIdentifier {
    SymbolDefine("define", "cd.define", null, IssMarkerFactory.CompilerDirective.SymbolDefine.SECTION, IssCompilerDirectiveSymbolDefineParameterType.class),
    SymbolUndefine("undef", "cd.undef", null, IssMarkerFactory.CompilerDirective.SymbolUndefine.SECTION, IssCompilerDirectiveSymbolUndefineParameterType.class),
    PreProcessor("preproc", "cd.preproc", null, IssMarkerFactory.CompilerDirective.PreProcessor.SECTION, IssCompilerDirectivePreProcessorParameterType.class),
    Include("include", "cd.preproc", null, IssMarkerFactory.CompilerDirective.Include.SECTION, IssCompilerDirectiveIncludeParameterType.class),
    ;

    public static IssCompilerDirectiveSectionType fromId(final String id) {
        return Stream.of(values())
                .filter(item -> item.id.equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }

    private final String id;
    private final String descriptionKey;
    private final Icon icon;
    private final IElementType elementType;
    private final Class<? extends IssCompilerDirectiveParameterIdentifier> parameterIdentifierClass;

    private IssCompilerDirectiveSectionType(String id, String descriptionKey, Icon icon, IElementType elementType, Class<? extends IssCompilerDirectiveParameterIdentifier> parameterIdentifierClass) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.icon = icon;
        this.elementType = elementType;
        this.parameterIdentifierClass = parameterIdentifierClass;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @NotNull
    @Override
    public IElementType getElementType() {
        return elementType;
    }

    @NotNull
    @Override
    public Class<? extends IssCompilerDirectiveParameterIdentifier> getParameterIdentifierClass() {
        return parameterIdentifierClass;
    }

    @NotNull
    @Override
    public IssCompilerDirectiveParameterIdentifier[] getParameterIdentifiers() {
        return IssCompilerDirectiveParameterIdentifier.getValues(parameterIdentifierClass);
    }

}

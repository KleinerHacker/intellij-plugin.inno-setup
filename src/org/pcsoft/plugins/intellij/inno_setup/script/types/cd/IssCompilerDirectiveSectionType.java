package org.pcsoft.plugins.intellij.inno_setup.script.types.cd;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveSymbolParameterType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveParameterIdentifier;

import javax.swing.Icon;
import java.util.stream.Stream;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public enum IssCompilerDirectiveSectionType implements IssCompilerDirectiveSectionIdentifier {
    Symbol("define", "cd.define", null, IssMarkerFactory.COMPILER_DIRECTIVE_SYMBOL_SECTION, IssCompilerDirectiveSymbolParameterType.class),
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

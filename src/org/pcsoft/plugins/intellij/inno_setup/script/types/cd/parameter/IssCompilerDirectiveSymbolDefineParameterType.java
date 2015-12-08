package org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value.IssCompilerDirectiveVisibility;

import java.util.function.Predicate;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public enum IssCompilerDirectiveSymbolDefineParameterType implements IssCompilerDirectiveParameterIdentifier {
    Visibility("Visibility", IssMarkerFactory.CompilerDirective.SymbolDefine.PARAMETER_VISIBILITY, 0, s -> IssCompilerDirectiveVisibility.fromId(s) != null),
    Identifier("Identifier", IssMarkerFactory.CompilerDirective.SymbolDefine.PARAMETER_IDENTIFIER, 1),
    Value("Value", IssMarkerFactory.CompilerDirective.SymbolDefine.PARAMETER_VALUE, 2)
    ;

    private final String name;
    private final IElementType elementType;
    private final int orderNumber;
    private final boolean optional;
    private final Predicate<String> parameterCheck;

    private IssCompilerDirectiveSymbolDefineParameterType(String name, IElementType elementType, int orderNumber) {
        this(name, elementType, orderNumber, null);
    }

    private IssCompilerDirectiveSymbolDefineParameterType(String name, IElementType elementType, int orderNumber, Predicate<String> parameterCheck) {
        this.name = name;
        this.elementType = elementType;
        this.orderNumber = orderNumber;
        this.optional = parameterCheck != null;
        this.parameterCheck = parameterCheck;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public IElementType getElementType() {
        return elementType;
    }

    @Override
    public int getOrderNumber() {
        return orderNumber;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @NotNull
    @Override
    public Predicate<String> isParameter() {
        return parameterCheck == null ? s -> true : parameterCheck;
    }


}

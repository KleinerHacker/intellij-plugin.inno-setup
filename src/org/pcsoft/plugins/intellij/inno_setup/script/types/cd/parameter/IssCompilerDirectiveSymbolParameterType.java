package org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssValueType;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public enum IssCompilerDirectiveSymbolParameterType implements IssCompilerDirectiveParameterIdentifier {
    Identifier("Identifier", IssMarkerFactory.COMPILER_DIRECTIVE_SYMBOL_IDENTIFIER_PARAMETER, IssValueType.DirectSingle, 0),
    Value("Value", IssMarkerFactory.COMPILER_DIRECTIVE_SYMBOL_VALUE_PARAMETER, IssValueType.String, 1)
    ;

    private final String name;
    private final IElementType elementType;
    private final IssValueType valueType;
    private final int orderNumber;

    private IssCompilerDirectiveSymbolParameterType(String name, IElementType elementType, IssValueType valueType, int orderNumber) {
        this.name = name;
        this.elementType = elementType;
        this.valueType = valueType;
        this.orderNumber = orderNumber;
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

    @NotNull
    @Override
    public IssValueType getValueType() {
        return valueType;
    }

    @Override
    public int getOrderNumber() {
        return orderNumber;
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public enum IssConstantType implements IssPropertyValue {
    Builtin(null, "constant.type.builtin", IssMarkerFactory.Constant.TYPE_BUILTIN, IssConstantTypeType.None),
    Message("cm", "constant.type.message", IssMarkerFactory.Constant.TYPE_MESSAGE, IssConstantTypeType.TypeName),
    Environment("%", "constant.type.environment", IssMarkerFactory.Constant.TYPE_ENVIRONMENT, IssConstantTypeType.SingleCharacter),
    CompilerDirective("#", "constant.type.compiler_directive", IssMarkerFactory.Constant.TYPE_COMPILER_DIRECTIVE, IssConstantTypeType.SingleCharacter),
    Drive("drive", "constant.type.drive", IssMarkerFactory.Constant.TYPE_DRIVE, IssConstantTypeType.TypeName),
    INI("ini", "constant.type.ini", IssMarkerFactory.Constant.TYPE_INI, IssConstantTypeType.TypeName),
    Registry("reg", "constant.type.reg", IssMarkerFactory.Constant.TYPE_REGISTRY, IssConstantTypeType.TypeName),
    Parameter("param", "constant.type.param", IssMarkerFactory.Constant.TYPE_PARAMETER, IssConstantTypeType.TypeName),
    ;

    @NotNull
    public static IssConstantType fromId(final String id) {
        final IssConstantType constantType = IssPropertyValue.findById(id, IssConstantType.class);
        return constantType == null ? Builtin : constantType;
    }

    private final String id, descriptionKey;
    private final IElementType elementType;
    private final IssConstantTypeType type;

    private IssConstantType(String id, String descriptionKey, @NotNull IElementType elementType, @NotNull IssConstantTypeType type) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.elementType = elementType;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @NotNull
    public IElementType getElementType() {
        return elementType;
    }

    @NotNull
    public IssConstantTypeType getType() {
        return type;
    }
}

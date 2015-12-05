package org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.IssMarkerFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public enum IssConstantType implements IssPropertyValue {
    Builtin(null, "constant.type.builtin", IssMarkerFactory.BUILTIN_CONSTANT),
    Message("cm", "constant.type.message", IssMarkerFactory.MESSAGE_CONSTANT),
    Environment("%", "constant.type.environment", IssMarkerFactory.ENVIRONMENT_CONSTANT),
    CompilerDirective("#", "constant.type.compiler_directive", IssMarkerFactory.COMPILER_DIRECTIVE_CONSTANT);

    @NotNull
    public static IssConstantType fromId(final String id) {
        final IssConstantType constantType = IssPropertyValue.findById(id, IssConstantType.class);
        return constantType == null ? Builtin : constantType;
    }

    private final String id, descriptionKey;
    private final IElementType elementType;

    private IssConstantType(String id, String descriptionKey, IElementType elementType) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.elementType = elementType;
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

    public IElementType getElementType() {
        return elementType;
    }
}

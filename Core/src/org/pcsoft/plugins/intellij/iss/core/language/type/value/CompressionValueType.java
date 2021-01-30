package org.pcsoft.plugins.intellij.iss.core.language.type.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.SpecialValueType;
import org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation.IsDeprecated;

public enum CompressionValueType implements SpecialValueType {
    Zip("zip"),
    Zip1("zip/1"),
    Zip2("zip/2"),
    Zip3("zip/3"),
    Zip4("zip/4"),
    Zip5("zip/5"),
    Zip6("zip/6"),
    Zip7("zip/7"),
    Zip8("zip/8"),
    Zip9("zip/9"),
    Lzma("lzma"),
    LzmaFast("lzma/fast"),
    LzmaNormal("lzma/normal"),
    LzmaMax("lzma/max"),
    LzmaUltra("lzma/ultra"),
    LzmaUltra64("lzma/ultra64"),
    Lzma2("lzma2"),
    Lzma2Fast("lzma2/fast"),
    Lzma2Normal("lzma2/normal"),
    Lzma2Max("lzma2/max"),
    Lzma2Ultra("lzma2/ultra"),
    Lzma2Ultra64("lzma2/ultra64"),
    None("none"),
    ;

    @NotNull
    private String name;
    private boolean deprecated;

    private CompressionValueType(@NotNull String name) {
        this.name = name;

        try {
            deprecated = getClass().getField(name()).getAnnotation(IsDeprecated.class) != null;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}

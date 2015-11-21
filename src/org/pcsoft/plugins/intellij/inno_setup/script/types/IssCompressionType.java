package org.pcsoft.plugins.intellij.inno_setup.script.types;

/**
 * Created by Christoph on 04.01.2015.
 */
public enum IssCompressionType implements IssPropertyValue {
    None("none", ""),
    ZIP("zip", ""),
    ZIP1("zip/1", ""),
    ZIP2("zip/2", ""),
    ZIP3("zip/3", ""),
    ZIP4("zip/4", ""),
    ZIP5("zip/5", ""),
    ZIP6("zip/6", ""),
    ZIP7("zip/7", ""),
    ZIP8("zip/8", ""),
    ZIP9("zip/9", ""),
    BZIP("bzip", ""),
    BZIP1("bzip/1", ""),
    BZIP2("bzip/2", ""),
    BZIP3("bzip/3", ""),
    BZIP4("bzip/4", ""),
    BZIP5("bzip/5", ""),
    BZIP6("bzip/6", ""),
    BZIP7("bzip/7", ""),
    BZIP8("bzip/8", ""),
    BZIP9("bzip/9", ""),
    LZMA("lzma", ""),
    LZMAFast("lzma/fast", ""),
    LZMANormal("lzma/normal", ""),
    LZMAMax("lzma/max", ""),
    LZMAUltra("lzma/ultra", ""),
    LZMAUltra64("lzma/ultra64", ""),
    LZMA2("lzma2", ""),
    LZMA2Fast("lzma2/fast", ""),
    LZMA2Normal("lzma2/normal", ""),
    LZMA2Max("lzma2/max", ""),
    LZMA2Ultra("lzma2/ultra", ""),
    LZMA2Ultra64("lzma2/ultra64", ""),
    ;

    public static IssCompressionType fromId(final String id) {
        return IssPropertyValue.findById(id, IssCompressionType.class);
    }

    private final String id, descriptionKey;
    private final boolean deprecated;

    private IssCompressionType(String id, String descriptionKey) {
        this(id, descriptionKey, false);
    }

    private IssCompressionType(String id, String descriptionKey, boolean deprecated) {
        this.id = id;
        this.descriptionKey = descriptionKey;
        this.deprecated = deprecated;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }
}

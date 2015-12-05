package org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPresentationHint;

/**
 * Created by pfeifchr on 03.12.2015.
 */
public enum IssBuiltinConstantType implements IssPresentationHint {
    Directory("Directory", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_BUILTIN_DIRECTORY),
    ShellFolder("Shell-Folder", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_BUILTIN_SHELL),
    Other(null, IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_CONSTANT_BUILTIN_OTHER);

    private final String text;
    private final TextAttributesKey textAttributesKey;

    private IssBuiltinConstantType(String text, TextAttributesKey textAttributesKey) {
        this.text = text;
        this.textAttributesKey = textAttributesKey;
    }

    public String getText() {
        return text;
    }

    @Override
    public TextAttributesKey getTextAttributesKey() {
        return textAttributesKey;
    }
}

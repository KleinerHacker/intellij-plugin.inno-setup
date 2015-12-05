package org.pcsoft.plugins.intellij.inno_setup.script.types.section;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPresentationHint;

/**
 * Created by Christoph on 21.11.2015.
 */
//TODO
public enum IssSetupPropertyClassifier implements IssPresentationHint {
    Compiler("Compiler-related", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_CF_COMPILER_STANDARD),
    Installer("Installer-related", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_CF_INSTALLER_STANDARD),
    Cosmetic("Cosmetic", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_CF_COSMETIC_STANDARD),
    Obsolete("Obsolete", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_PROPERTY_NAME_CF_OBSOLETE)
    ;

    private final String text;
    private final TextAttributesKey textAttributesKey;

    private IssSetupPropertyClassifier(String text, TextAttributesKey textAttributesKey) {
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

package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;

/**
 * Created by Christoph on 20.11.2015.
 */
public final class IssRunHighlightingColorFactory {

    public static final TextAttributesKey CONSOLE_IDE = TextAttributesKey.createTextAttributesKey(
            "CONSOLE_IDE",
            new TextAttributes(JBColor.BLUE, null, null, null, 0)
    );

    private IssRunHighlightingColorFactory() {
    }
}

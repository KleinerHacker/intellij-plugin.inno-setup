package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

import java.awt.Font;

/**
 * Created by Christoph on 20.11.2015.
 */
public class IssPropertyFontValueElement extends IssPropertyValueElement {

    public IssPropertyFontValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public Font getFont() {
        try {
            return new Font(getText(), Font.PLAIN, 8);
        } catch (Exception e) {
            return null;
        }
    }
}

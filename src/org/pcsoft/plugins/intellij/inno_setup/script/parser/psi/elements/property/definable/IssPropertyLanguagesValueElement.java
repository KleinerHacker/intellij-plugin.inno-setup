package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

import java.util.Locale;

/**
 * Created by Christoph on 04.01.2015.
 */
public class IssPropertyLanguagesValueElement extends IssPropertyValueElement {

    public IssPropertyLanguagesValueElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public Locale getLocale() {
        try {
            return Locale.forLanguageTag(getText());
        } catch (Exception e) {
            return null;
        }
    }
}

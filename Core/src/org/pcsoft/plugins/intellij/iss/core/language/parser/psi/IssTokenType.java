package org.pcsoft.plugins.intellij.iss.core.language.parser.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.IssLanguage;

/**
 * Created by Christoph on 30.09.2016.
 */
public class IssTokenType extends IElementType {
    public IssTokenType(@NotNull @NonNls String debugName) {
        super(debugName, IssLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "IssTokenType." + super.toString();
    }
}

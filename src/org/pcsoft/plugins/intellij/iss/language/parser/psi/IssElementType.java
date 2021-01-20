package org.pcsoft.plugins.intellij.iss.language.parser.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.IssLanguage;

/**
 * Created by Christoph on 30.09.2016.
 */
public class IssElementType extends IElementType {
    public IssElementType(@NotNull @NonNls String debugName) {
        super(debugName, IssLanguage.INSTANCE);
    }
}

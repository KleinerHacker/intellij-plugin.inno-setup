package org.pcsoft.plugins.intellij.iss.language.common;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes;

/**
 * Created by Christoph on 02.10.2016.
 */
public class IssBraceMatcher implements PairedBraceMatcher{
    @Override
    public BracePair[] getPairs() {
        return new BracePair[] {
                new BracePair(IssCustomTypes.BRACES_CORNER_OPEN, IssCustomTypes.BRACES_CORNER_CLOSE, false)
        };
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType, @Nullable IElementType iElementType1) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile psiFile, int i) {
        return 0;
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.others;

import com.intellij.codeInsight.highlighting.PairedBraceMatcherAdapter;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public class IssBraceMatcher extends PairedBraceMatcherAdapter {

    public IssBraceMatcher() {
        super(new PairedBraceMatcher() {
            @Override
            public BracePair[] getPairs() {
                return new BracePair[] {
                        new BracePair(IssTokenFactory.BRACE_CURLY_START, IssTokenFactory.BRACE_CURLY_END, false),
                        new BracePair(IssTokenFactory.BRACE_BRACKET_START, IssTokenFactory.BRACE_BRACKET_END, false),
                        new BracePair(IssTokenFactory.BRACE_START, IssTokenFactory.BRACE_END, false),
                        new BracePair(IssTokenFactory.BRACE_ANGLE_START, IssTokenFactory.BRACE_ANGLE_END, false)
                };
            }

            @Override
            public boolean isPairedBracesAllowedBeforeType(IElementType iElementType, @Nullable IElementType iElementType1) {
                return true;
            }

            @Override
            public int getCodeConstructStart(PsiFile psiFile, int i) {
                return 0;
            }
        }, IssLanguage.INSTANCE);
    }
}

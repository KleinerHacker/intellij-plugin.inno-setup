package org.pcsoft.plugins.intellij.iss.core.language.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.core.language.parser.IssCustomTypes;
import org.pcsoft.plugins.intellij.iss.core.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.core.language.parser.lexer.IssLexerAdapter;

/**
 * Created by Christoph on 02.10.2016.
 */
public class IssSyntaxHighlighter extends SyntaxHighlighterBase {

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new IssLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
        if (iElementType.equals(IssCustomTypes.OPERATOR)) {
            return new TextAttributesKey[]{IssHighlighting.OPERATOR};
        } else if (iElementType.equals(IssCustomTypes.SPLITTER)) {
            return new TextAttributesKey[]{IssHighlighting.SPLITTER};
        } else if (iElementType.equals(IssCustomTypes.BRACES_CORNER_CLOSE) || iElementType.equals(IssCustomTypes.BRACES_CORNER_OPEN) ||
                iElementType.equals(IssCustomTypes.BRACES_CURLY_CLOSE) || iElementType.equals(IssCustomTypes.BRACES_CURLY_OPEN)) {
            return new TextAttributesKey[]{IssHighlighting.BRACES};
        } else if (iElementType.equals(IssCustomTypes.COMMENT)) {
            return new TextAttributesKey[]{IssHighlighting.COMMENT};
        } else if (iElementType.equals(IssCustomTypes.QUOTE)) {
            return new TextAttributesKey[]{IssHighlighting.STRING};
        } else if (iElementType.equals(IssGenTypes.NUMBER) || iElementType.equals(IssGenTypes.VERSION)) {
            return new TextAttributesKey[]{IssHighlighting.NUMBER};
        }

        return new TextAttributesKey[]{};
    }
}

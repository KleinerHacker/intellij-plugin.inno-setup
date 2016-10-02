package org.pcsoft.plugins.intellij.iss.language.highlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes;
import org.pcsoft.plugins.intellij.iss.language.parser.lexer.IssLexerAdapter;

/**
 * Created by Christoph on 02.10.2016.
 */
public class IssSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey SEPARATOR = TextAttributesKey.createTextAttributesKey("ISS_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey SPLITTER = TextAttributesKey.createTextAttributesKey("ISS_SPLITTER", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("ISS_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("ISS_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("ISS_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("ISS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("ISS_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new IssLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
        if (iElementType.equals(IssCustomTypes.OPERATOR)) {
            return new TextAttributesKey[]{SEPARATOR};
        } else if (iElementType.equals(IssCustomTypes.SPLITTER)) {
            return new TextAttributesKey[]{SPLITTER};
        } else if (iElementType.equals(IssCustomTypes.BRACES_CORNER_CLOSE) || iElementType.equals(IssCustomTypes.BRACES_CORNER_OPEN)) {
            return new TextAttributesKey[]{BRACES};
        } else if (iElementType.equals(IssCustomTypes.COMMENT)) {
            return new TextAttributesKey[]{COMMENT};
        } else if (iElementType.equals(IssCustomTypes.QUOTE) || iElementType.equals(IssGenTypes.TEXT)) {
            return new TextAttributesKey[]{STRING};
        } else if (iElementType.equals(IssGenTypes.NUMBER)) {
            return new TextAttributesKey[]{NUMBER};
        }

        return new TextAttributesKey[]{};
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.syntax;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Christoph
 * Date: 13.07.13
 * Time: 20:43
 * TODO: Write Summary
 */
public final class IssSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> keys = new HashMap<IElementType, TextAttributesKey>();

    private static IssSyntaxHighlighter instance;

    public static IssSyntaxHighlighter getInstance() {
        if (instance == null) {
            instance = new IssSyntaxHighlighter();
        }

        return instance;
    }

    static {
        keys.put(IssTokenFactory.BAD_CHARACTER, IssLanguageHighlightingColorFactory.SYNTAX_BAD_CHARACTER);
        putTokenSet(IssTokenFactory.TS_COMMENTS, IssLanguageHighlightingColorFactory.SYNTAX_COMMENT);
        keys.put(IssTokenFactory.STRING, IssLanguageHighlightingColorFactory.SYNTAX_STRING);
        keys.put(IssTokenFactory.NUMBER, IssLanguageHighlightingColorFactory.SYNTAX_NUMBER);
        keys.put(IssTokenFactory.COMPILER_DIRECTIVE, IssLanguageHighlightingColorFactory.SYNTAX_CD);
        keys.put(IssTokenFactory.SECTION_TITLE, IssLanguageHighlightingColorFactory.SYNTAX_SECTION_TITLE);
        putTokenSet(IssTokenFactory.TS_OPERATORS, IssLanguageHighlightingColorFactory.SYNTAX_OPERATORS);
    }

    private static void putTokenSet(TokenSet tokenSet, TextAttributesKey key) {
        for (IElementType type : tokenSet.getTypes()) {
            keys.put(type, key);
        }
    }

    private IssSyntaxHighlighter() {
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return IssSyntaxHighlightingLexer.getInstance();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
        return pack(keys.get(iElementType));
    }
}

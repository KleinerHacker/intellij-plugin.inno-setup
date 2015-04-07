package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.syntax;

import com.intellij.lexer.LayeredLexer;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssLexerAdapter;

/**
 * User: Christoph
 * Date: 13.07.13
 * Time: 21:28
 * TODO: Write Summary
 */
public final class IssSyntaxHighlightingLexer extends LayeredLexer {

    private static IssSyntaxHighlightingLexer instance;
    public static IssSyntaxHighlightingLexer getInstance() {
        if (instance == null) {
            instance = new IssSyntaxHighlightingLexer();
        }

        return instance;
    }

    private IssSyntaxHighlightingLexer() {
        super(IssLexerAdapter.INSTANCE);
    }
}

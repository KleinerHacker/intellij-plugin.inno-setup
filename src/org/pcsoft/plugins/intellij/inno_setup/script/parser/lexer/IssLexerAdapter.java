package org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * Created by Christoph on 13.12.2014.
 */
public class IssLexerAdapter extends FlexAdapter {

    public static final IssLexerAdapter INSTANCE = new IssLexerAdapter();

    private IssLexerAdapter() {
        super(new _IssLexer((Reader) null));
    }
}

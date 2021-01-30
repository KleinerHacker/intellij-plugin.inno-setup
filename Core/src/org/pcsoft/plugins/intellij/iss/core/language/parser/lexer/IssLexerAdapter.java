package org.pcsoft.plugins.intellij.iss.core.language.parser.lexer;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

/**
 * Created by Christoph on 02.10.2016.
 */
public class IssLexerAdapter extends FlexAdapter {
    public IssLexerAdapter() {
        super(new _IssLexer((Reader) null));
    }
}

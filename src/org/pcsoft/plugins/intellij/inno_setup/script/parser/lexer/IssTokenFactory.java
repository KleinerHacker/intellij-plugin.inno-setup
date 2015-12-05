package org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;

/**
 * Created by Christoph on 12.12.2014.
 */
public final class IssTokenFactory {

    private static final Language LANGUAGE = IssLanguage.INSTANCE;

    public static final IElementType COMMENT = new IElementType("COMMENT", LANGUAGE);
    public static final IElementType CRLF = new IElementType("CRLF", LANGUAGE);
    public static final IElementType WHITE_SPACE = new IElementType("WHITE_SPACE", LANGUAGE);
    public static final IElementType BAD_CHARACTER = new IElementType("BAD_CHARACTER", LANGUAGE);

    public static final IElementType NAME = new IElementType("NAME", LANGUAGE);
    public static final IElementType WORD = new IElementType("WORD", LANGUAGE);

    public static final IElementType NUMBER = new IElementType("NUMBER", LANGUAGE);

    public static final IElementType OPERATOR_COLON = new IElementType("OP_COLON", LANGUAGE);
    public static final IElementType OPERATOR_SEMICOLON = new IElementType("OP_SEMICOLON", LANGUAGE);
    public static final IElementType OPERATOR_EQUAL = new IElementType("OP_EQUAL", LANGUAGE);
    public static final IElementType OPERATOR_POINT = new IElementType("OP_POINT", LANGUAGE);
    public static final IElementType OPERATOR_COMMA = new IElementType("OP_COMMA", LANGUAGE);
    public static final IElementType OPERATOR_PIPE = new IElementType("OP_PIPE", LANGUAGE);

    public static final IElementType QUOTE = new IElementType("QUOTE", LANGUAGE);
    public static final IElementType SHARP = new IElementType("SHARP", LANGUAGE);
    public static final IElementType PERCENT = new IElementType("PERCENT", LANGUAGE);
    public static final IElementType BRACE_CURLY_START = new IElementType("BRACE_CURLY_START", LANGUAGE);
    public static final IElementType BRACE_CURLY_END = new IElementType("BRACE_CURLY_START", LANGUAGE);
    public static final IElementType BRACE_BRACKET_START = new IElementType("BRACE_BRACKET_START", LANGUAGE);
    public static final IElementType BRACE_BRACKET_END = new IElementType("BRACE_BRACKET_START", LANGUAGE);
    public static final IElementType BRACE_START = new IElementType("BRACE_START", LANGUAGE);
    public static final IElementType BRACE_END = new IElementType("BRACE_START", LANGUAGE);

    public static final TokenSet TS_WHITE_SPACES = TokenSet.create(WHITE_SPACE);
    public static final TokenSet TS_COMMENTS = TokenSet.create(COMMENT);
    public static final TokenSet TS_OPERATORS = TokenSet.create(OPERATOR_COLON, OPERATOR_EQUAL, OPERATOR_SEMICOLON,
            OPERATOR_POINT);

    public static final TokenSet TS_BRACE_CURLY = TokenSet.create(BRACE_CURLY_START, BRACE_CURLY_END);
    public static final TokenSet TS_BRACE_BRACKET = TokenSet.create(BRACE_BRACKET_START, BRACE_BRACKET_END);
    public static final TokenSet TS_BRACE = TokenSet.create(BRACE_START, BRACE_END);

    private IssTokenFactory() {
    }
}

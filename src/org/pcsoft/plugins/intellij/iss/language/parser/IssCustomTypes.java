package org.pcsoft.plugins.intellij.iss.language.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.pcsoft.plugins.intellij.iss.language.parser.psi.IssTokenType;

/**
 * Created by Christoph on 30.09.2016.
 */
public interface IssCustomTypes {
    IElementType COMMENT = new IssTokenType("COMMENT");

    IElementType BRACES_CORNER_OPEN = new IssTokenType("BRACES_CORNER_OPEN");
    IElementType BRACES_CORNER_CLOSE = new IssTokenType("BRACES_CORNER_CLOSE");

    IElementType BRACES_CURLY_OPEN = new IssTokenType("BRACES_CURLY_OPEN");
    IElementType BRACES_CURLY_CLOSE = new IssTokenType("BRACES_CURLY_CLOSE");

    IElementType BRACES_ANGLE_OPEN = new IssTokenType("BRACES_ANGLE_OPEN");
    IElementType BRACES_ANGLE_CLOSE = new IssTokenType("BRACES_ANGLE_CLOSE");

    IElementType QUOTE = new IssTokenType("QUOTE");
    IElementType SPLITTER = new IssTokenType("SPLITTER");
    IElementType OPERATOR = new IssTokenType("OPERATOR");

    TokenSet WHITE_SPACE_SET = TokenSet.create(com.intellij.psi.TokenType.WHITE_SPACE);
    TokenSet COMMENT_SET = TokenSet.create(COMMENT);
}

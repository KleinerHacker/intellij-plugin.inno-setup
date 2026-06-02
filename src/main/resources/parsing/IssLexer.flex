package org.pcsoft.intellij.plugin.inno_setup.language.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssTypes;
import com.intellij.psi.TokenType;

%%

%class _IssLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

ALPHA      = [A-Za-z_]
IDENT_CHAR = [A-Za-z0-9_.\-]
IDENTIFIER = {ALPHA}{IDENT_CHAR}*
NUMBER     = [0-9]+
WHITESPACE = [ \t]+
NEWLINE    = \r?\n
STRING     = \"[^\"]*\"
COMMENT    = ";"[^\r\n]*
VALUE_CHAR = [^\r\n{};:=\"()\t ]

%state VALUE

%%

<YYINITIAL> {
    {COMMENT}    { return IssTypes.COMMENT; }
    "["          { return IssTypes.LBRACKET; }
    "]"          { return IssTypes.RBRACKET; }
    "("          { return IssTypes.LPAREN; }
    ")"          { return IssTypes.RPAREN; }
    "#"          { return IssTypes.HASH; }
    {IDENTIFIER} { return IssTypes.IDENTIFIER; }
    "="          { yybegin(VALUE); return IssTypes.EQ; }
    ":"          { yybegin(VALUE); return IssTypes.COLON; }
    ";"          { return IssTypes.SEMICOLON; }
    {NEWLINE}    { return IssTypes.CRLF; }
    {WHITESPACE} { return TokenType.WHITE_SPACE; }
    [^]          { return TokenType.BAD_CHARACTER; }
}

<VALUE> {
    {STRING}      { return IssTypes.STRING; }
    "{"           { return IssTypes.LBRACE; }
    "}"           { return IssTypes.RBRACE; }
    "("           { return IssTypes.LPAREN; }
    ")"           { return IssTypes.RPAREN; }
    {NUMBER}      { return IssTypes.NUMBER; }
    ":"           { return IssTypes.COLON; }
    "#"           { return IssTypes.HASH; }
    "="           { return IssTypes.EQ; }
    ";"           { yybegin(YYINITIAL); return IssTypes.SEMICOLON; }
    {NEWLINE}     { yybegin(YYINITIAL); return IssTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {IDENTIFIER}  { return IssTypes.IDENTIFIER; }
    {VALUE_CHAR}+ { return IssTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

package org.pcsoft.intellij.plugin.inno_setup.language.parsing;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes;
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
COMMENT    = (";" | "//")[^\r\n]*
VALUE_CHAR = [^\r\n{};:=\"()\t ]

%state VALUE
%state IN_STRING
%state IN_STRING_CONSTANT

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
    {NEWLINE}    { return IssTypes.CRLF; }
    {WHITESPACE} { return TokenType.WHITE_SPACE; }
    [^]          { return TokenType.BAD_CHARACTER; }
}

<VALUE> {
    "{"           { return IssTypes.LBRACE; }
    "}"           { return IssTypes.RBRACE; }
    "("           { return IssTypes.LPAREN; }
    ")"           { return IssTypes.RPAREN; }
    "\""          { yybegin(IN_STRING); return IssTypes.QUOTE; }
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

<IN_STRING> {
    "{"           { yybegin(IN_STRING_CONSTANT); return IssTypes.LBRACE; }
    "\""          { yybegin(VALUE); return IssTypes.QUOTE; }
    {NEWLINE}     { yybegin(YYINITIAL); return IssTypes.CRLF; }
    [^\"{\r\n]+   { return IssTypes.STRING_PART; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING_CONSTANT> {
    "}"           { yybegin(IN_STRING); return IssTypes.RBRACE; }
    {NUMBER}      { return IssTypes.NUMBER; }
    ":"           { return IssTypes.COLON; }
    "#"           { return IssTypes.HASH; }
    "="           { return IssTypes.EQ; }
    {NEWLINE}     { yybegin(YYINITIAL); return IssTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {IDENTIFIER}  { return IssTypes.IDENTIFIER; }
    {VALUE_CHAR}+ { return IssTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

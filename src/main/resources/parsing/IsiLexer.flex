package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiTypes;
import com.intellij.psi.TokenType;

%%

%class _IsiLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

ALPHA      = [A-Za-z_]
IDENT_CHAR = [A-Za-z0-9_.\-]
IDENTIFIER = {ALPHA}{IDENT_CHAR}*
NUMBER     = [0-9]+(\.[0-9]+)*
WHITESPACE = [ \t]+
NEWLINE    = \r?\n
COMMENT    = (";" | "//")[^\r\n]*
VALUE_CHAR = [^\r\n{};:=\"()#\t ]

%state VALUE
%state IN_STRING
%state IN_STRING_CONSTANT

%%

<YYINITIAL> {
    {COMMENT}           { return IsiTypes.COMMENT; }
    "["                 { return IsiTypes.LBRACKET; }
    "]"                 { return IsiTypes.RBRACKET; }
    "("                 { return IsiTypes.LPAREN; }
    ")"                 { return IsiTypes.RPAREN; }
    "#" [^\r\n]*        { return IsiTypes.PREPROCESSOR_LINE; }
    {IDENTIFIER}        { return IsiTypes.IDENTIFIER; }
    "="                 { yybegin(VALUE); return IsiTypes.EQ; }
    ":"                 { yybegin(VALUE); return IsiTypes.COLON; }
    {NEWLINE}           { return IsiTypes.CRLF; }
    {WHITESPACE}        { return TokenType.WHITE_SPACE; }
    [^]                 { return TokenType.BAD_CHARACTER; }
}

<VALUE> {
    "{"           { return IsiTypes.LBRACE; }
    "}"           { return IsiTypes.RBRACE; }
    "("           { return IsiTypes.LPAREN; }
    ")"           { return IsiTypes.RPAREN; }
    "\""          { yybegin(IN_STRING); return IsiTypes.QUOTE; }
    {NUMBER}      { return IsiTypes.NUMBER; }
    ":"           { return IsiTypes.COLON; }
    "#"           { return IsiTypes.HASH; }
    "="           { return IsiTypes.EQ; }
    ";"           { yybegin(YYINITIAL); return IsiTypes.SEMICOLON; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsiTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {IDENTIFIER}  { return IsiTypes.IDENTIFIER; }
    {VALUE_CHAR}+ { return IsiTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING> {
    "{"           { yybegin(IN_STRING_CONSTANT); return IsiTypes.LBRACE; }
    "\""          { yybegin(VALUE); return IsiTypes.QUOTE; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsiTypes.CRLF; }
    [^\"{\r\n]+   { return IsiTypes.STRING_PART; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING_CONSTANT> {
    "}"           { yybegin(IN_STRING); return IsiTypes.RBRACE; }
    {NUMBER}      { return IsiTypes.NUMBER; }
    ":"           { return IsiTypes.COLON; }
    "#"           { return IsiTypes.HASH; }
    "="           { return IsiTypes.EQ; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsiTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {IDENTIFIER}  { return IsiTypes.IDENTIFIER; }
    {VALUE_CHAR}+ { return IsiTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

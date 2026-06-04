package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes;
import com.intellij.psi.TokenType;

%%

%class _IsppLexer
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
VALUE_CHAR = [^\r\n{};:=\"()#\t ]

%state IN_STRING
%state IN_STRING_CONSTANT

%%

<YYINITIAL> {
    "#"           { return IsppTypes.HASH; }
    {IDENTIFIER}  { return IsppTypes.IDENTIFIER; }
    {NUMBER}      { return IsppTypes.NUMBER; }
    "\""          { yybegin(IN_STRING); return IsppTypes.QUOTE; }
    "{"           { return IsppTypes.LBRACE; }
    "}"           { return IsppTypes.RBRACE; }
    "("           { return IsppTypes.LPAREN; }
    ")"           { return IsppTypes.RPAREN; }
    "="           { return IsppTypes.EQ; }
    ":"           { return IsppTypes.COLON; }
    ";"           { return IsppTypes.SEMICOLON; }
    {NEWLINE}     { return IsppTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {VALUE_CHAR}+ { return IsppTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING> {
    "{"           { yybegin(IN_STRING_CONSTANT); return IsppTypes.LBRACE; }
    "\""          { yybegin(YYINITIAL); return IsppTypes.QUOTE; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsppTypes.CRLF; }
    [^\"{\r\n]+   { return IsppTypes.STRING_PART; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING_CONSTANT> {
    "}"           { yybegin(IN_STRING); return IsppTypes.RBRACE; }
    {IDENTIFIER}  { return IsppTypes.IDENTIFIER; }
    {NUMBER}      { return IsppTypes.NUMBER; }
    "#"           { return IsppTypes.HASH; }
    ":"           { return IsppTypes.COLON; }
    "="           { return IsppTypes.EQ; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {VALUE_CHAR}+ { return IsppTypes.VALUE_CHAR; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsppTypes.CRLF; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

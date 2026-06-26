package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorTypes;
import com.intellij.psi.TokenType;

%%

%class _IsPreprocessorLexer
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
VALUE_CHAR = [^\r\n\[\]{};:=\"()#\t ]

%state IN_STRING
%state IN_STRING_CONSTANT

%%

<YYINITIAL> {
    "#"           { return IsPreprocessorTypes.HASH; }
    {IDENTIFIER}  { return IsPreprocessorTypes.IDENTIFIER; }
    {NUMBER}      { return IsPreprocessorTypes.NUMBER; }
    "\""          { yybegin(IN_STRING); return IsPreprocessorTypes.QUOTE; }
    "{"           { return IsPreprocessorTypes.LBRACE; }
    "}"           { return IsPreprocessorTypes.RBRACE; }
    "("           { return IsPreprocessorTypes.LPAREN; }
    ")"           { return IsPreprocessorTypes.RPAREN; }
    "["           { return IsPreprocessorTypes.LBRACKET; }
    "]"           { return IsPreprocessorTypes.RBRACKET; }
    "="           { return IsPreprocessorTypes.EQ; }
    ":"           { return IsPreprocessorTypes.COLON; }
    ";"           { return IsPreprocessorTypes.SEMICOLON; }
    {NEWLINE}     { return IsPreprocessorTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {VALUE_CHAR}+ { return IsPreprocessorTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING> {
    "{"           { yybegin(IN_STRING_CONSTANT); return IsPreprocessorTypes.LBRACE; }
    "\""          { yybegin(YYINITIAL); return IsPreprocessorTypes.QUOTE; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsPreprocessorTypes.CRLF; }
    [^\"{\r\n]+   { return IsPreprocessorTypes.STRING_PART; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING_CONSTANT> {
    "}"           { yybegin(IN_STRING); return IsPreprocessorTypes.RBRACE; }
    {IDENTIFIER}  { return IsPreprocessorTypes.IDENTIFIER; }
    {NUMBER}      { return IsPreprocessorTypes.NUMBER; }
    "#"           { return IsPreprocessorTypes.HASH; }
    ":"           { return IsPreprocessorTypes.COLON; }
    "="           { return IsPreprocessorTypes.EQ; }
    "["           { return IsPreprocessorTypes.VALUE_CHAR; }
    "]"           { return IsPreprocessorTypes.VALUE_CHAR; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {VALUE_CHAR}+ { return IsPreprocessorTypes.VALUE_CHAR; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsPreprocessorTypes.CRLF; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

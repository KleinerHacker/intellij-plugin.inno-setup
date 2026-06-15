package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionTypes;
import com.intellij.psi.TokenType;

%%

%class _IsSectionLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

ALPHA      = [A-Za-z_]
IDENT_CHAR = [A-Za-z0-9_.\-]
IDENTIFIER = {ALPHA}{IDENT_CHAR}*
// Identifiers must generally start with a letter. Flag identifiers are the sole exception: they may
// start with a digit as long as a letter follows (e.g. the flag "64bit"). Flags only occur in the
// value area, so this relaxed form is used exclusively in the VALUE state. A purely numeric token
// stays a NUMBER, because a letter is required here.
VALUE_IDENTIFIER = ({ALPHA} | [0-9]+ {ALPHA}) {IDENT_CHAR}*
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
    {COMMENT}           { return IsSectionTypes.COMMENT; }
    "["                 { return IsSectionTypes.LBRACKET; }
    "]"                 { return IsSectionTypes.RBRACKET; }
    "("                 { return IsSectionTypes.LPAREN; }
    ")"                 { return IsSectionTypes.RPAREN; }
    "#" [^\r\n]*        { return IsSectionTypes.HASH_LINE; }
    {IDENTIFIER}        { return IsSectionTypes.IDENTIFIER; }
    "="                 { yybegin(VALUE); return IsSectionTypes.EQ; }
    ":"                 { yybegin(VALUE); return IsSectionTypes.COLON; }
    {NEWLINE}           { return IsSectionTypes.CRLF; }
    {WHITESPACE}        { return TokenType.WHITE_SPACE; }
    [^]                 { return TokenType.BAD_CHARACTER; }
}

<VALUE> {
    "{"           { return IsSectionTypes.LBRACE; }
    "}"           { return IsSectionTypes.RBRACE; }
    "("           { return IsSectionTypes.LPAREN; }
    ")"           { return IsSectionTypes.RPAREN; }
    "\""          { yybegin(IN_STRING); return IsSectionTypes.QUOTE; }
    {NUMBER}      { return IsSectionTypes.NUMBER; }
    ":"           { return IsSectionTypes.COLON; }
    "#"           { return IsSectionTypes.HASH; }
    "="           { return IsSectionTypes.EQ; }
    ";"           { yybegin(YYINITIAL); return IsSectionTypes.SEMICOLON; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsSectionTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {VALUE_IDENTIFIER} { return IsSectionTypes.IDENTIFIER; }
    {VALUE_CHAR}+ { return IsSectionTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}
// NOTE: VALUE_IDENTIFIER (digit-leading flags) is intentionally NOT used below; constant
// identifiers inside {…} keep the standard letter-leading IDENTIFIER form.

<IN_STRING> {
    "{"           { yybegin(IN_STRING_CONSTANT); return IsSectionTypes.LBRACE; }
    "\""          { yybegin(VALUE); return IsSectionTypes.QUOTE; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsSectionTypes.CRLF; }
    [^\"{\r\n]+   { return IsSectionTypes.STRING_PART; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING_CONSTANT> {
    "}"           { yybegin(IN_STRING); return IsSectionTypes.RBRACE; }
    {NUMBER}      { return IsSectionTypes.NUMBER; }
    ":"           { return IsSectionTypes.COLON; }
    "#"           { return IsSectionTypes.HASH; }
    "="           { return IsSectionTypes.EQ; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsSectionTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {IDENTIFIER}  { return IsSectionTypes.IDENTIFIER; }
    {VALUE_CHAR}+ { return IsSectionTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

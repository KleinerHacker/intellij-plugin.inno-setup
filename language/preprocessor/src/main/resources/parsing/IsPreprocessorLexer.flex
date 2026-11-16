package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorTypes;
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
// The one-character directive shorthands: #? (#if), #^ (#else), #. (#endif) and #% (#env).
SHORTHAND  = [\?\^\.\%]
// A directive continues on the next line when the current line ends with a backslash; spaces/tabs
// behind that backslash are tolerated (the formatter strips them). The continuation carries no
// meaning for the grammar and is reported as whitespace.
CONTINUATION = \\[ \t]*{NEWLINE}

%state IN_STRING
%state IN_STRING_CONSTANT
// Directly behind the "#" that starts a fragment when a shorthand character follows.
%state AFTER_HASH

%%

<YYINITIAL> {
    {CONTINUATION} { return TokenType.WHITE_SPACE; }
    // A fragment is exactly one directive and starts with its "#". Only that leading "#" may be followed by a
    // shorthand keyword; a "#" further right (e.g. the "#%" in a {#%Name} constant) keeps its old meaning.
    "#" / {SHORTHAND} { if (zzStartRead == 0) yybegin(AFTER_HASH); return IsPreprocessorTypes.HASH; }
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

// The shorthand keyword is reported as an IDENTIFIER, so it is the directive keyword like `if` or `endif`.
<AFTER_HASH> {
    {SHORTHAND}   { yybegin(YYINITIAL); return IsPreprocessorTypes.IDENTIFIER; }
    [^]           { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
}

<IN_STRING> {
    "{"           { yybegin(IN_STRING_CONSTANT); return IsPreprocessorTypes.LBRACE; }
    "\""          { yybegin(YYINITIAL); return IsPreprocessorTypes.QUOTE; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsPreprocessorTypes.CRLF; }
    [^\"{\r\n]+   { return IsPreprocessorTypes.STRING_PART; }
    [^]           { return TokenType.BAD_CHARACTER; }
}

<IN_STRING_CONSTANT> {
    {CONTINUATION} { return TokenType.WHITE_SPACE; }
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

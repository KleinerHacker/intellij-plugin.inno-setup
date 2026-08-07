package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes;
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
// A section entry may be continued on the next physical line by ending it with a backslash (used e.g. by the
// [ISSigKeys] entries of the official examples). The continuation carries no meaning of its own, so it is
// swallowed like whitespace and the entry keeps being one logical line for the parser.
CONTINUATION = \\ [ \t]* {NEWLINE}
VALUE_CHAR = [^\r\n{};:=\"()#\t ]
// A preprocessor line may be continued on the following line by ending it with a backslash, so the
// opaque HASH_LINE token spans every continued line as well. Trailing spaces/tabs behind the
// backslash are tolerated (the formatter strips them).
HASH_LINE  = "#" ([^\r\n]* \\ [ \t]* {NEWLINE})* [^\r\n]*
// One line of free-form Pascal inside [Code]. Stops before a "//" comment so that one keeps its own token;
// a single "/" (division) stays part of the line via the second alternative.
CODE_LINE  = ("/" [^/\r\n] | [^\r\n/])+
// Inside [Code] a ";" is Pascal's statement separator, not a comment — only "//" starts one.
COMMENT_PASCAL = "//" [^\r\n]*

%state VALUE
%state IN_STRING
%state IN_STRING_CONSTANT
%state IN_STRING_CONSTANT_NESTED
// Section-header states. The name of the section decides how its body is lexed, so the header is walked
// through its own states instead of being remembered in a Java field: the IntelliJ lexer restarts
// incremental relexing from the state alone, and a field would not be part of it.
%state IN_HEADER
%state IN_CODE_HEADER
%state IN_CODE

%%

<YYINITIAL> {
    {COMMENT}           { return IsSectionTypes.COMMENT; }
    "["                 { yybegin(IN_HEADER); return IsSectionTypes.LBRACKET; }
    "]"                 { return IsSectionTypes.RBRACKET; }
    "("                 { return IsSectionTypes.LPAREN; }
    ")"                 { return IsSectionTypes.RPAREN; }
    {HASH_LINE}         { return IsSectionTypes.HASH_LINE; }
    {CONTINUATION}      { return TokenType.WHITE_SPACE; }
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
    // Must come before VALUE_CHAR: a lone "\" is a normal path character, only "\" at the end of the line
    // continues the entry.
    {CONTINUATION} { return TokenType.WHITE_SPACE; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsSectionTypes.CRLF; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {VALUE_IDENTIFIER} { return IsSectionTypes.IDENTIFIER; }
    {VALUE_CHAR}+ { return IsSectionTypes.VALUE_CHAR; }
    [^]           { return TokenType.BAD_CHARACTER; }
}
// A section header "[Name]". Everything is tokenized exactly as before — only the state differs, so that
// the header of a [Code] section can hand over to IN_CODE once its line ends.
<IN_HEADER> {
    {IDENTIFIER}  { if (yytext().toString().equalsIgnoreCase("code")) yybegin(IN_CODE_HEADER);
                    return IsSectionTypes.IDENTIFIER; }
    "]"           { yybegin(YYINITIAL); return IsSectionTypes.RBRACKET; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {NEWLINE}     { yybegin(YYINITIAL); return IsSectionTypes.CRLF; }
    [^]           { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
}

// Rest of the "[Code]" header line: the body starts after its line break.
<IN_CODE_HEADER> {
    "]"           { return IsSectionTypes.RBRACKET; }
    {COMMENT}     { return IsSectionTypes.COMMENT; }
    {WHITESPACE}  { return TokenType.WHITE_SPACE; }
    {NEWLINE}     { yybegin(IN_CODE); return IsSectionTypes.CRLF; }
    [^]           { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
}

// The body of [Code] is free-form Pascal Script — it uses neither "Key=Value" nor "Key: Value" syntax, so
// every line is handed to the parser as one opaque CODE_LINE token instead of being tokenized as an entry.
// The section ends where the next section header starts, i.e. at a "[" in column 1 (exactly as ISCC ends it).
<IN_CODE> {
    {NEWLINE}            { return IsSectionTypes.CRLF; }
    // Hand the header back to YYINITIAL without consuming anything of it.
    ^ "[" [^\r\n]*       { yypushback(yylength()); yybegin(YYINITIAL); }
    // An ISPP directive stays an ISPP directive inside [Code]. Only a "#" that starts the line qualifies —
    // a "#" further right is a Pascal character literal (#13#10) and is already part of a CODE_LINE.
    ^ [ \t]+ / "#"       { return TokenType.WHITE_SPACE; }
    {HASH_LINE}          { return IsSectionTypes.HASH_LINE; }
    {COMMENT_PASCAL}     { return IsSectionTypes.COMMENT; }
    {CODE_LINE}          { return IsSectionTypes.CODE_LINE; }
    [^]                  { return IsSectionTypes.CODE_LINE; }
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

// A constant inside a quoted string. A constant argument may itself be a constant
// ("{group}\{cm:UninstallProgram,{cm:MyAppName}}"), which IN_STRING_CONSTANT_NESTED covers: without it the
// inner "}" would already end the outer constant and put the lexer back into the string.
// The nesting is tracked by these two states, so exactly ONE level of nesting is supported — the depth every
// real script uses. A JFlex lexer cannot count deeper, because the IntelliJ lexer restarts incremental
// relexing from the state alone and a counter field would not be part of it.
<IN_STRING_CONSTANT> {
    "{"           { yybegin(IN_STRING_CONSTANT_NESTED); return IsSectionTypes.LBRACE; }
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

<IN_STRING_CONSTANT_NESTED> {
    "}"           { yybegin(IN_STRING_CONSTANT); return IsSectionTypes.RBRACE; }
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

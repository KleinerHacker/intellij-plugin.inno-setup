package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.template;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.template.psi.IsTemplateTypes;
import com.intellij.psi.TokenType;

%%

%class _IsTemplateLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

ALPHA      = [A-Za-z_]
IDENT_CHAR = [A-Za-z0-9_.\-]
IDENTIFIER = {ALPHA}{IDENT_CHAR}*
NEWLINE    = \r?\n
WHITESPACE = [ \t]+
// Any other run of characters is opaque free text (a .ist file is free text apart from preprocessor
// lines, brackets/parens and identifiers).
TEXT       = [^\r\n \t#\[\]()A-Za-z_]+

%%

<YYINITIAL> {
    "#" [^\r\n]*        { return IsTemplateTypes.HASH_LINE; }
    "["                 { return IsTemplateTypes.LBRACKET; }
    "]"                 { return IsTemplateTypes.RBRACKET; }
    "("                 { return IsTemplateTypes.LPAREN; }
    ")"                 { return IsTemplateTypes.RPAREN; }
    {IDENTIFIER}        { return IsTemplateTypes.IDENTIFIER; }
    {NEWLINE}           { return IsTemplateTypes.CRLF; }
    {WHITESPACE}        { return TokenType.WHITE_SPACE; }
    {TEXT}              { return IsTemplateTypes.TEXT; }
    [^]                 { return IsTemplateTypes.TEXT; }
}

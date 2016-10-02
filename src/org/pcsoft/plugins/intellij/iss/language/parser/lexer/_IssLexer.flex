package org.pcsoft.plugins.intellij.iss.language.parser.lexer;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes.*;
import static org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes.*;

%%

%{
  public _IssLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _IssLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\r | \n | \r\n
WHITE_SPACE=" "
NAME=[A-Za-z_]{1}[A-Za-z0-9\-_]*
NUMBER=([0-9]*\.)?[0-9]+
TEXT=[^\"]*
COMMENT="--"[^\r\n]*

BRACES_CORNER_OPEN="["
BRACES_CORNER_CLOSE="]"
QUOTE=\"
SPLITTER=";"
OPERATOR="="

%state YYSTRING

%%
<YYINITIAL> {
  {WHITE_SPACE}             { return com.intellij.psi.TokenType.WHITE_SPACE; }
  {EOL}                     { return EOL; }
  {COMMENT}                 { return COMMENT; }

  {BRACES_CORNER_OPEN}      { return BRACES_CORNER_OPEN; }
  {BRACES_CORNER_CLOSE}     { return BRACES_CORNER_CLOSE; }

  <YYSTRING> {
    {QUOTE}                 { if (yystate() == YYSTRING) yybegin(YYINITIAL); else yybegin(YYSTRING); return QUOTE; }
  }
  {SPLITTER}                { return SPLITTER; }
  {OPERATOR}                { return OPERATOR; }

  {NUMBER}                  { return NUMBER; }
  {NAME}                    { return NAME; }
}
<YYSTRING> {
  {TEXT}                    { return TEXT; }
}

[^]                         { return com.intellij.psi.TokenType.BAD_CHARACTER; }

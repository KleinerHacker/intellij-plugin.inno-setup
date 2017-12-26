package org.pcsoft.plugins.intellij.iss.language.parser.lexer;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static org.pcsoft.plugins.intellij.iss.language.parser.IssGenTypes.*;
import static org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes.*;

%%

%{
  private boolean isInCode = false;
  private int constCounter = 0;
  private int beforeConstMode;

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
%ignorecase

EOL=\r | \n | \r\n
WHITE_SPACE=" "
NAME=[A-Za-z_]{1}[A-Za-z0-9\-_\\\/]*[A-Za-z0-9\_]?
HEX_BYTE=[0-9a-fA-F]{2}
NUMBER=[0-9]+|\$({HEX_BYTE})+
VERSION=([0-9]+\.)*[0-9]+
STRING=([^\"\{] | \{\{ | \"\")+
VALUE_TEXT=([^\r|\n|\{] | \{\{ | \"\")+
CONST_TEXT=([^\{\}\,\|\:\%\#] | \{\{ | \"\")+
COMMENT=";"[^\r|\n|\r\n]*{EOL}


%state YYSTRING
%state YYVALUE
%state YYCODE
%state YYTITLE
%state YYCONST
%state YYFILE

%%
<YYINITIAL> {
  <YYTITLE> {
      {WHITE_SPACE}         { return com.intellij.psi.TokenType.WHITE_SPACE; }
  }
  ^{EOL}                    { return com.intellij.psi.TokenType.WHITE_SPACE; }
  {EOL}                     { return EOL; }
  ^{COMMENT}                { return COMMENT; }

  \[                        { yybegin(YYTITLE); return BRACES_CORNER_OPEN; }
  <YYTITLE> {
      \]                    { return BRACES_CORNER_CLOSE; }
  }
  <YYVALUE, YYSTRING, YYCONST> {
      \{                    { if (constCounter <= 0) { beforeConstMode = yystate(); yybegin(YYCONST); } constCounter++; return BRACES_CURLY_OPEN; }
  }
  \<                        { yybegin(YYFILE); return BRACES_ANGLE_OPEN; }
  <YYFILE> {
      \>                    { yybegin(YYINITIAL); return BRACES_ANGLE_CLOSE; }
  }

  <YYSTRING> {
      \"                    { if (yystate() == YYSTRING) yybegin(YYINITIAL); else yybegin(YYSTRING); return QUOTE; }
   }

  ";"                       { yybegin(YYINITIAL); return SPLITTER; }
  "="                       { yybegin(YYVALUE); return OPERATOR; }
  <YYCONST> {
      "#"                   { return OPERATOR; }
      ":"                   { return OPERATOR; }
      "|"                   { return OPERATOR; }
      ","                   { return SPLITTER; }
      "%"                   { return OPERATOR; }
  }
  <YYFILE> {
      "."                   { return SPLITTER; }
  }

  <YYVALUE> {
  {NUMBER}                  { return NUMBER; }
  {VERSION}                 { return VERSION; }
      <YYSTRING, YYCONST, YYFILE> {
          {NAME}            { return NAME; }
      }
  {HEX_BYTE}                { return HEX_BYTE; }
  }
}
<YYTITLE> {
  {EOL}                     { yybegin(isInCode ? YYCODE : YYINITIAL); isInCode = false; return EOL; }
  {NAME}                    { isInCode = yytext().toString().equalsIgnoreCase("code"); return NAME; }
}
<YYVALUE> {
  {EOL}                     { yybegin(YYINITIAL); return EOL; }
  {VALUE_TEXT}              { return TEXT; }
}
<YYCONST> {
  //Only here to interpret it only in constant values
  \}                        { if (constCounter > 0) {constCounter--; if (constCounter <= 0) yybegin(beforeConstMode); } return BRACES_CURLY_CLOSE; }
  {CONST_TEXT}              { return TEXT; }
}
<YYSTRING> {
  {STRING}                  { return STRING; }
}
<YYCODE> {
  {WHITE_SPACE}|{EOL}       { return com.intellij.psi.TokenType.WHITE_SPACE; }
  [^]+                      { return CODE; }
}

[^]                         { return com.intellij.psi.TokenType.BAD_CHARACTER; }

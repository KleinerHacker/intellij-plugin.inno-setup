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
STRING=[^\"]+
TEXT=[\\/?!|#+*~äöüÄÖÜß().:,;A-Za-z0-9\-_&%$§\"\'°\^<>" "\[\]]+
COMMENT=";"[^\r|\n|\r\n]*{EOL}


%state YYSTRING
%state YYVALUE

%%
<YYINITIAL> {
  {WHITE_SPACE}             { return com.intellij.psi.TokenType.WHITE_SPACE; }
  ^{EOL}                    { return com.intellij.psi.TokenType.WHITE_SPACE; }
  {EOL}                     { return EOL; }
  ^{COMMENT}                { return COMMENT; }

  \[                        { return BRACES_CORNER_OPEN; }
  \]                        { return BRACES_CORNER_CLOSE; }
  <YYVALUE> {
      \{                    { return BRACES_CURLY_OPEN; }
      \}                    { return BRACES_CURLY_CLOSE; }
  }

  <YYSTRING> {
      \"                    { if (yystate() == YYSTRING) yybegin(YYINITIAL); else yybegin(YYSTRING); return QUOTE; }
   }

  ";"                       { yybegin(YYINITIAL); return SPLITTER; }
  "="                       { yybegin(YYVALUE); return OPERATOR; }
  ":"                       { return OPERATOR; }

  {NUMBER}                  { return NUMBER; }
  <YYVALUE> {
      {NAME}                { return NAME; }
  }
}
<YYVALUE> {
  {EOL}                     { yybegin(YYINITIAL); return EOL; }
  {TEXT}                    { return TEXT; }
}
<YYSTRING> {
  {STRING}                  { return STRING; }
}

[^]                         { return com.intellij.psi.TokenType.BAD_CHARACTER; }

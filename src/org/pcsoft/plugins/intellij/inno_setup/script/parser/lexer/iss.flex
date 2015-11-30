package org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import com.intellij.psi.TokenType;

%%

%class _IssLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

CRLF= \n|\r|\r\n
WHITE_SPACE=[\ \t\f]
COMMENT=;.*

OPERATOR_COLON=:
OPERATOR_SEMICOLON=;
OPERATOR_EQUAL=\=
OPERATOR_POINT=\.

NAME=[A-Za-z0-9_]+
WORD=[^\ \t\f\n\r(\r\n)\=\:\;\"\.]+
EXTENDED_WORD=[^\ \t\f\n\r(\r\n)\"]+
COMPILER_DIRECTIVE=#{NAME}
SECTION_TITLE=\[{NAME}\]

STRING=\"[^\n\r(\r\n)\"]*\"
DECIMAL=\.[0-9]+
INTEGER=[0-9]+
NUMBER=[-+]?{INTEGER}{DECIMAL}?

%state ALLOW_ALL
%state CODE

%%

{COMPILER_DIRECTIVE}                                        { return IssTokenFactory.COMPILER_DIRECTIVE; }

^\[Code\]                                                   { yybegin(CODE); return IssTokenFactory.SECTION_TITLE; }
^{SECTION_TITLE}                                            { yybegin(YYINITIAL); return IssTokenFactory.SECTION_TITLE; }
{OPERATOR_COLON}                                            { return IssTokenFactory.OPERATOR_COLON; }
{OPERATOR_SEMICOLON}                                        { return IssTokenFactory.OPERATOR_SEMICOLON; }
{OPERATOR_EQUAL}                                            { yybegin(ALLOW_ALL); return IssTokenFactory.OPERATOR_EQUAL; }
{OPERATOR_POINT}                                        { return IssTokenFactory.OPERATOR_POINT; }
{STRING}                                                    { return IssTokenFactory.STRING;}
{NAME}                                                      { return IssTokenFactory.NAME; }
{WORD}                                                      { return IssTokenFactory.WORD; }
{NUMBER}                                                    { return IssTokenFactory.NUMBER; }
<ALLOW_ALL> {
    {EXTENDED_WORD}                                         { return IssTokenFactory.WORD; }
}
<CODE> {
    {EXTENDED_WORD}                                         { return IssTokenFactory.WORD; }
}

{CRLF}                                                      { yybegin(YYINITIAL); return IssTokenFactory.CRLF; }

{WHITE_SPACE}+                                              { return IssTokenFactory.WHITE_SPACE; }

^{COMMENT}{CRLF}                                            { return IssTokenFactory.COMMENT; }

.                                                           { return IssTokenFactory.BAD_CHARACTER; }
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
OPERATOR_COMMA=\,
OPERATOR_PIPE=\|

QUOTE=\"
BRACE_CURLY_START=\{
BRACE_CURLY_END=\}
BRACE_BRACKET_START=\[
BRACE_BRACKET_END=\]
BRACE_START=\(
BRACE_END=\)
SHARP=#
PERCENT=%

NAME=[A-Za-z0-9_]+
WORD=[^\ \t\f\n\r(\r\n)\=\:\;\"\.\{\}#\[\]\(\)\%\,\|]+

DECIMAL=\.[0-9]+
INTEGER=[0-9]+
NUMBER=[-+]?{INTEGER}{DECIMAL}?

%%

{OPERATOR_COLON}                                            { return IssTokenFactory.OPERATOR_COLON; }
{OPERATOR_SEMICOLON}                                        { return IssTokenFactory.OPERATOR_SEMICOLON; }
{OPERATOR_EQUAL}                                            { return IssTokenFactory.OPERATOR_EQUAL; }
{OPERATOR_POINT}                                            { return IssTokenFactory.OPERATOR_POINT; }
{OPERATOR_COMMA}                                            { return IssTokenFactory.OPERATOR_COMMA; }
{OPERATOR_PIPE}                                             { return IssTokenFactory.OPERATOR_PIPE; }
{NUMBER}                                                    { return IssTokenFactory.NUMBER; }
{SHARP}                                                     { return IssTokenFactory.SHARP; }
{PERCENT}                                                   { return IssTokenFactory.PERCENT; }
{QUOTE}                                                     { return IssTokenFactory.QUOTE; }
{BRACE_CURLY_START}                                         { return IssTokenFactory.BRACE_CURLY_START; }
{BRACE_CURLY_END}                                           { return IssTokenFactory.BRACE_CURLY_END; }
{BRACE_BRACKET_START}                                       { return IssTokenFactory.BRACE_BRACKET_START; }
{BRACE_BRACKET_END}                                         { return IssTokenFactory.BRACE_BRACKET_END; }
{BRACE_START}                                               { return IssTokenFactory.BRACE_START; }
{BRACE_END}                                                 { return IssTokenFactory.BRACE_END; }
{NAME}                                                      { return IssTokenFactory.NAME; }
{WORD}                                                      { return IssTokenFactory.WORD; }

{CRLF}                                                      { yybegin(YYINITIAL); return IssTokenFactory.CRLF; }

{WHITE_SPACE}+                                              { return IssTokenFactory.WHITE_SPACE; }

^{COMMENT}{CRLF}                                            { return IssTokenFactory.COMMENT; }

.                                                           { return IssTokenFactory.BAD_CHARACTER; }
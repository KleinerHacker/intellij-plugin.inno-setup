package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssConstantType;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public final class IssParserValueUtility {

    //region Constant Value
    public static void parseSingleConstantValue(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker constantMark = psiBuilder.mark();
        psiBuilder.advanceLexer();

        final IssConstantType constantType = getConstantType(psiBuilder);
        final IElementType constantElementType;
        switch (constantType) {
            case Builtin:
                constantElementType = handleBuiltinConstant(psiBuilder);
                break;
            case Message:
                constantElementType = handleMessageConstant(psiBuilder);
                break;
            case CompilerDirective:
                constantElementType = handleCompilerDirectiveConstant(psiBuilder);
                break;
            case Environment:
                constantElementType = handleEnvironmentConstant(psiBuilder);
                break;
            default:
                throw new RuntimeException();
        }

        constantMark.done(constantElementType);
    }

    private static IElementType handleBuiltinConstant(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker constantNameMark = psiBuilder.mark();
        while (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END && psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
            psiBuilder.advanceLexer();
        }
        constantNameMark.done(IssMarkerFactory.CONSTANT_NAME);
        if (!psiBuilder.eof()) {
            psiBuilder.advanceLexer();
        } else {
            psiBuilder.error("'}' expected");
        }

        return IssMarkerFactory.BUILTIN_CONSTANT;
    }

    private static IElementType handleMessageConstant(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker constantTypeMark = psiBuilder.mark();
        psiBuilder.advanceLexer(); //'cm'
        constantTypeMark.done(IssMarkerFactory.CONSTANT_TYPE);
        psiBuilder.advanceLexer(); //':'

        final PsiBuilder.Marker constantNameMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        constantNameMark.done(IssMarkerFactory.CONSTANT_NAME);

        //Arguments
        if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COMMA && !psiBuilder.eof()) {
            psiBuilder.advanceLexer(); //','
            //Build error for ',}' or line end or ',,'
            if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_END || psiBuilder.getTokenType() == IssTokenFactory.CRLF ||
                    psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COMMA || psiBuilder.eof()) {
                psiBuilder.error("Argument expected");
            } else {
                final PsiBuilder.Marker argumentsMark = psiBuilder.mark();
                //Loop for all arguments until '}'
                while (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END && psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
                    final PsiBuilder.Marker argumentMark = psiBuilder.mark();
                    //Loop for one argument with any content until ',' or '}'
                    while (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END && psiBuilder.getTokenType() != IssTokenFactory.CRLF &&
                            psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_COMMA && !psiBuilder.eof()) {
                        parseSingleValue(psiBuilder);
                    }
                    argumentMark.done(IssMarkerFactory.CONSTANT_ARGUMENT);
                    //goon if comma
                    if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COMMA && !psiBuilder.eof()) {
                        psiBuilder.advanceLexer();
                        //Build error for ',}' or line end or ',,'
                        if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_END || psiBuilder.getTokenType() == IssTokenFactory.CRLF ||
                                psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COMMA || psiBuilder.eof()) {
                            psiBuilder.error("Argument expected");
                        }
                    }
                }
                argumentsMark.done(IssMarkerFactory.CONSTANT_ARGUMENTS);
            }
        }

        if (!psiBuilder.eof()) {
            psiBuilder.advanceLexer();
        } else {
            psiBuilder.error("'}' expected");
        }

        return IssMarkerFactory.MESSAGE_CONSTANT;
    }

    private static IElementType handleEnvironmentConstant(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker constantTypeMark = psiBuilder.mark();
        psiBuilder.advanceLexer(); //'%'
        constantTypeMark.done(IssMarkerFactory.CONSTANT_TYPE);

        final PsiBuilder.Marker constantNameMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        constantNameMark.done(IssMarkerFactory.CONSTANT_NAME);

        //Default Value
        if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_PIPE && !psiBuilder.eof()) {
            psiBuilder.advanceLexer();
            //Check for '|}' or line end
            if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_PIPE || psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_END ||
                    psiBuilder.getTokenType() == IssTokenFactory.CRLF || psiBuilder.eof()) {
                psiBuilder.error("Default Value expected");
            } else {
                final PsiBuilder.Marker argumentsMark = psiBuilder.mark();
                final PsiBuilder.Marker argumentMark = psiBuilder.mark();
                while (psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_PIPE && psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END &&
                        psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
                    parseSingleValue(psiBuilder);
                }
                argumentMark.done(IssMarkerFactory.CONSTANT_ARGUMENT);
                argumentsMark.done(IssMarkerFactory.CONSTANT_ARGUMENTS);
            }
        }

        if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_END && !psiBuilder.eof()) {
            psiBuilder.advanceLexer();
        } else {
            psiBuilder.error("'}' expected");
        }

        return IssMarkerFactory.ENVIRONMENT_CONSTANT;
    }

    private static IElementType handleCompilerDirectiveConstant(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker constantTypeMark = psiBuilder.mark();
        psiBuilder.advanceLexer(); //'#'
        constantTypeMark.done(IssMarkerFactory.CONSTANT_TYPE);

        final PsiBuilder.Marker constantNameMark = psiBuilder.mark();
        while (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END && psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
            psiBuilder.advanceLexer();
        }
        constantNameMark.done(IssMarkerFactory.CONSTANT_NAME);
        if (!psiBuilder.eof()) {
            psiBuilder.advanceLexer();
        } else {
            psiBuilder.error("'}' expected");
        }

        return IssMarkerFactory.COMPILER_DIRECTIVE_CONSTANT;
    }

    private static IssConstantType getConstantType(PsiBuilder psiBuilder) {
        if (psiBuilder.getTokenType() == IssTokenFactory.NAME) {
            final String constantTypeName = psiBuilder.getTokenText();
            final PsiBuilder.Marker constantTypeMark = psiBuilder.mark();
            psiBuilder.advanceLexer();
            if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COLON) {
                //Rollback to start of constant ('{')
                constantTypeMark.rollbackTo();

                return IssConstantType.fromId(constantTypeName);
            } else {
                //Rollback to start of constant ('{')
                constantTypeMark.rollbackTo();
                //Fallback
                return IssConstantType.Builtin;
            }
        } else {
            return IssConstantType.fromId(psiBuilder.getTokenText());
        }
    }
    //endregion

    //region String Value
    public static void parseSingleStringValue(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker stringMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        while (psiBuilder.getTokenType() != IssTokenFactory.QUOTE && psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
            if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_START) {
                parseSingleConstantValue(psiBuilder);
            } else {
                psiBuilder.advanceLexer();
            }
        }
        if (!psiBuilder.eof()) {
            psiBuilder.advanceLexer();
        } else {
            psiBuilder.error("'\"' expected");
        }
        stringMark.done(IssMarkerFactory.STRING);
    }
    //endregion

    /**
     * Parse a single value like a normal direct value, a string (with quotes) or a constant (with '{')
     * @param psiBuilder
     */
    public static void parseSingleValue(PsiBuilder psiBuilder) {
        if (psiBuilder.getTokenType() == IssTokenFactory.QUOTE) {
            IssParserValueUtility.parseSingleStringValue(psiBuilder);
        } else if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_START) {
            IssParserValueUtility.parseSingleConstantValue(psiBuilder);
        } else {
            psiBuilder.advanceLexer();
        }
    }

    private IssParserValueUtility() {
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssConstantType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssConstantTypeType;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public final class IssParserValueUtility {

    private static void parseEscapingValue(PsiBuilder psiBuilder, IElementType endToken) {
        while (psiBuilder.getTokenType() != endToken && psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
            parseSingleValue(psiBuilder);
        }
        if (psiBuilder.getTokenType() == endToken && !psiBuilder.eof()) {
            psiBuilder.advanceLexer();
        }
    }

    //region Constant Value
    private static void parseSingleConstantValue(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker constantMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        //Check for escaping
        if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_START) {
            psiBuilder.advanceLexer();
            parseEscapingValue(psiBuilder, IssTokenFactory.BRACE_CURLY_END);
            constantMark.done(IssMarkerFactory.ESCAPING);
            return;
        }

        final IssConstantType constantType = getConstantType(psiBuilder);
        if (constantType == null) {
            while (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END && psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
                psiBuilder.advanceLexer();
            }
            if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_END && !psiBuilder.eof()) {
                psiBuilder.advanceLexer();
            }
            constantMark.error("Unknown constant type!");
            return;
        }

        parseConstant(psiBuilder, constantType);

        if (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END) {
            psiBuilder.error("'{' expected!");
            if (!psiBuilder.eof()) {
                psiBuilder.advanceLexer();
            }
        } else {
            psiBuilder.advanceLexer();
        }

        constantMark.done(constantType.getElementType());
    }

    private static void parseConstant(PsiBuilder psiBuilder, IssConstantType constantType) {
        //Parse type
        if (constantType.getType() != IssConstantTypeType.None) {
            final PsiBuilder.Marker constantTypeMark = psiBuilder.mark();
            switch (constantType.getType()) {
                case SingleCharacter:
                    psiBuilder.advanceLexer(); //'X'
                    constantTypeMark.done(IssMarkerFactory.Constant.TYPE);
                    break;
                case TypeName:
                    psiBuilder.advanceLexer();//'abc'
                    constantTypeMark.done(IssMarkerFactory.Constant.TYPE);
                    psiBuilder.advanceLexer();//':'
                    break;
                default:
                    throw new RuntimeException();
            }
        }

        //Parse name
        final PsiBuilder.Marker constantNameMark = psiBuilder.mark();
        parseConstantValue(psiBuilder);
        constantNameMark.done(IssMarkerFactory.Constant.NAME);

        //Parse arguments
        if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COMMA) {
            psiBuilder.advanceLexer();//','
            final PsiBuilder.Marker argumentsMark = psiBuilder.mark();
            while (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END && psiBuilder.getTokenType() != IssTokenFactory.CRLF &&
                    psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_PIPE && !psiBuilder.eof()) {
                final PsiBuilder.Marker argumentMark = psiBuilder.mark();
                parseConstantValue(psiBuilder);
                argumentMark.done(IssMarkerFactory.Constant.ARGUMENT);

                if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COMMA) {
                    psiBuilder.advanceLexer();//Goon
                }
            }
            argumentsMark.done(IssMarkerFactory.Constant.ARGUMENTS);
        }

        //Parse default value
        if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_PIPE) {
            psiBuilder.advanceLexer();//'|'
            final PsiBuilder.Marker defaultValueMark = psiBuilder.mark();
            parseConstantValue(psiBuilder);
            defaultValueMark.done(IssMarkerFactory.Constant.DEFAULT_VALUE);
        }
    }

    private static void parseConstantValue(PsiBuilder psiBuilder) {
        if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_END || psiBuilder.getTokenType() == IssTokenFactory.CRLF ||
                psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COMMA || psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_PIPE || psiBuilder.eof()) {
            psiBuilder.error("Argument expected!");
            if (!psiBuilder.eof()) {
                psiBuilder.advanceLexer();
            }
            return;
        }

        while (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END && psiBuilder.getTokenType() != IssTokenFactory.CRLF &&
                psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_COMMA && psiBuilder.getTokenType() != IssTokenFactory.OPERATOR_PIPE && !psiBuilder.eof()) {
            parseSingleValue(psiBuilder);
        }
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
    private static void parseSingleStringValue(PsiBuilder psiBuilder) {
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

    //region File Link Value
    private static void parseSingleFileLinkValue(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker fileLinkMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        while (psiBuilder.getTokenType() != IssTokenFactory.BRACE_ANGLE_END && psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
            if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_CURLY_START) {
                parseSingleConstantValue(psiBuilder);
            } else {
                psiBuilder.advanceLexer();
            }
        }

        if (psiBuilder.getTokenType() != IssTokenFactory.BRACE_ANGLE_END) {
            psiBuilder.error("'>' expected");
        } else {
            psiBuilder.advanceLexer();
        }
        fileLinkMark.done(IssMarkerFactory.FILE_LINK);
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
        } else if (psiBuilder.getTokenType() == IssTokenFactory.BRACE_ANGLE_START) {
            IssParserValueUtility.parseSingleFileLinkValue(psiBuilder);
        } else {
            psiBuilder.advanceLexer();
        }
    }

    private IssParserValueUtility() {
    }
}

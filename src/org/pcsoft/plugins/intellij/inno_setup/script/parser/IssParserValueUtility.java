package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssConstantType;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public final class IssParserValueUtility {

    public static void parseSingleConstantValue(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker constantMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        if (psiBuilder.getTokenType() == IssTokenFactory.SHARP) {
            final PsiBuilder.Marker constantTypeMark = psiBuilder.mark();
            psiBuilder.advanceLexer();
            constantTypeMark.done(IssMarkerFactory.CONSTANT_TYPE);
            final PsiBuilder.Marker constantNameMark = psiBuilder.mark();
            psiBuilder.advanceLexer();
            constantNameMark.done(IssMarkerFactory.CONSTANT_NAME);
            if (psiBuilder.getTokenType() != IssTokenFactory.BRACE_CURLY_END) {
                psiBuilder.error("'}' expected");
                constantMark.drop();
                return;
            }
            psiBuilder.advanceLexer();
            constantMark.done(IssMarkerFactory.COMPILER_DIRECTIVE_CONSTANT);
        } else {
            final IElementType constantElementType = getConstantTypeElement(psiBuilder);

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
            constantMark.done(constantElementType);
        }
    }

    private static IElementType getConstantTypeElement(PsiBuilder psiBuilder) {
        final IElementType constantElementType;

        if (psiBuilder.getTokenType() == IssTokenFactory.NAME) {
            final String constantTypeName = psiBuilder.getTokenText();
            final PsiBuilder.Marker constantTypeMark = psiBuilder.mark();
            psiBuilder.advanceLexer();
            if (psiBuilder.getTokenType() == IssTokenFactory.OPERATOR_COLON) {
                final IssConstantType constantType = IssConstantType.fromId(constantTypeName);
                if (constantType == null) {
                    constantTypeMark.rollbackTo();
                    constantElementType = IssMarkerFactory.BUILTIN_CONSTANT;
                } else {
                    constantTypeMark.done(IssMarkerFactory.CONSTANT_TYPE);
                    psiBuilder.advanceLexer();

                    switch (constantType) {
                        case Message:
                            constantElementType = IssMarkerFactory.MESSAGE_CONSTANT;
                            break;
                        case Builtin:
                            constantElementType = IssMarkerFactory.BUILTIN_CONSTANT;
                            break;
                        default:
                            throw new RuntimeException();
                    }
                }
            } else {
                //Rollback to start of constant ('{')
                constantTypeMark.rollbackTo();
                //Fallback
                constantElementType = IssMarkerFactory.BUILTIN_CONSTANT;
            }
        } else {
            constantElementType = IssMarkerFactory.BUILTIN_CONSTANT;
        }

        return constantElementType;
    }

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

    private IssParserValueUtility() {
    }
}

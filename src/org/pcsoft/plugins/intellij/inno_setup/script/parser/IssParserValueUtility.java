package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;

/**
 * Created by pfeifchr on 01.12.2015.
 */
public final class IssParserValueUtility {

    public static void parseSingleConstantValue(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker constantMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        if (psiBuilder.getTokenType() == IssTokenFactory.SHARP) {
            psiBuilder.advanceLexer();
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
            constantMark.done(IssMarkerFactory.BUILTIN_CONSTANT);
        }
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

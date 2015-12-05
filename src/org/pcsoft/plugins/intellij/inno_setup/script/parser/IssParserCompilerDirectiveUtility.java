package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.IssCompilerDirectiveSectionType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.parameter.IssCompilerDirectiveParameterIdentifier;

/**
 * Created by Christoph on 14.12.2014.
 */
final class IssParserCompilerDirectiveUtility {

    public static void parseCompilerDirective(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker cdSectionMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        if (psiBuilder.getTokenType() != IssTokenFactory.NAME && psiBuilder.getTokenType() != IssTokenFactory.WORD) {
            psiBuilder.advanceLexer();
            cdSectionMark.error("Compiler Directive expected!");
            return;
        }

        final IssCompilerDirectiveSectionType compilerDirectiveType = IssCompilerDirectiveSectionType.fromId(psiBuilder.getTokenText());
        if (compilerDirectiveType == null) {
            while (psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
                psiBuilder.advanceLexer();
            }
            cdSectionMark.error("Unknown compiler directive!");
            return;
        }

        //CD Key Word
        final PsiBuilder.Marker cdMark = psiBuilder.mark();
        psiBuilder.advanceLexer();
        cdMark.done(IssMarkerFactory.COMPILER_DIRECTIVE);

        //Parameters
        final PsiBuilder.Marker cdParametersMark = psiBuilder.mark();
        final IssCompilerDirectiveParameterIdentifier[] parameters = IssCompilerDirectiveParameterIdentifier.getValues(compilerDirectiveType.getParameterIdentifierClass());
        int counter = 0;
        while (psiBuilder.getTokenType() != IssTokenFactory.CRLF && !psiBuilder.eof()) {
            final PsiBuilder.Marker cdValueMark = psiBuilder.mark();
            IssParserValueUtility.parseSingleValue(psiBuilder);
            if (parameters == null || parameters.length <= counter) {
                cdValueMark.error("Unknown parameter for this compiler directive: " + compilerDirectiveType.getId());
            } else {
                cdValueMark.done(parameters[counter].getElementType());
            }
            counter++;
        }
        cdParametersMark.done(IssMarkerFactory.COMPILER_DIRECTIVE_PARAMETERS);

        cdSectionMark.done(compilerDirectiveType.getElementType());
    }

    private IssParserCompilerDirectiveUtility() {
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.PsiBuilder;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;

/**
 * Created by Christoph on 14.12.2014.
 */
final class IssParserCompilerDirectiveUtility {

    public static void parseCompilerDirective(PsiBuilder psiBuilder) {
        final PsiBuilder.Marker cdMark = psiBuilder.mark();
        {
            while (!psiBuilder.eof() && psiBuilder.getTokenType() != IssTokenFactory.CRLF) {
                //TODO
                psiBuilder.advanceLexer();
            }

            if (!psiBuilder.eof() && psiBuilder.getTokenType() == IssTokenFactory.CRLF) {
                psiBuilder.advanceLexer();
            }

            cdMark.done(IssMarkerFactory.COMPILER_DIRECTIVE_SECTION);
        }
    }

    private IssParserCompilerDirectiveUtility() {
    }
}

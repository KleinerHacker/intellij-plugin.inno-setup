package org.pcsoft.plugins.intellij.inno_setup.script.others;

import com.intellij.codeInsight.editorActions.QuoteHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;

/**
 * Created by pfeifchr on 02.12.2015.
 */
public class IssQuoteHandler implements QuoteHandler {

    @Override
    public boolean isClosingQuote(HighlighterIterator highlighterIterator, int i) {
//        System.out.println("********** " + i + " / " + highlighterIterator.getStart() + "-" + highlighterIterator.getEnd() + " / " + highlighterIterator.getTokenType());
        return highlighterIterator.getTokenType() == IssTokenFactory.QUOTE;
    }

    @Override
    public boolean isOpeningQuote(HighlighterIterator highlighterIterator, int i) {
//        System.out.println("########## " + i + " / " + highlighterIterator.getStart() + "-" + highlighterIterator.getEnd() + " / " + highlighterIterator.getTokenType());
        return highlighterIterator.getTokenType() == IssTokenFactory.QUOTE;
    }

    @Override
    public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator highlighterIterator, int i) {
//        System.out.println("++++++++++ " + i + " / " + highlighterIterator.getStart() + "-" + highlighterIterator.getEnd() + " / " + highlighterIterator.getTokenType());
        while (highlighterIterator.getTokenType() != IssTokenFactory.CRLF && !highlighterIterator.atEnd()) {
            highlighterIterator.advance();
            if (highlighterIterator.getTokenType() == IssTokenFactory.QUOTE)
                return false;
        }

        return true;
    }

    @Override
    public boolean isInsideLiteral(HighlighterIterator highlighterIterator) {
//        System.out.println("---------- " + highlighterIterator.getStart() + "-" + highlighterIterator.getEnd() + " / " + highlighterIterator.getTokenType());

        int counter=0;
        while (highlighterIterator.getTokenType() != IssTokenFactory.CRLF && !highlighterIterator.atEnd()) {
            highlighterIterator.advance();
            if (highlighterIterator.getTokenType() == IssTokenFactory.QUOTE) {
                counter++;
            }
        }

        return counter % 2 != 0;
    }
}

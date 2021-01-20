package org.pcsoft.plugins.intellij.iss.language.common;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import org.pcsoft.plugins.intellij.iss.language.parser.IssCustomTypes;

/**
 * Created by Christoph on 02.10.2016.
 */
public class IssQuoteMatcher extends SimpleTokenSetQuoteHandler {
    public IssQuoteMatcher() {
        super(IssCustomTypes.QUOTE);
    }
}

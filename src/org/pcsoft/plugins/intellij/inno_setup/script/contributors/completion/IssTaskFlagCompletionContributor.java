package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyFlagsElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTaskFlagCompletionContributor extends IssFlagCompletionContributor<IssTaskPropertyFlagsElement> {
    public IssTaskFlagCompletionContributor() {
        super(IssTaskPropertyFlagsElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssTaskFlag.values();
    }
}

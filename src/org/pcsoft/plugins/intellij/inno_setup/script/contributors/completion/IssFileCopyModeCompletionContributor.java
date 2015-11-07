package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyCopyModeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileCopyMode;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import java.util.Collection;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFileCopyModeCompletionContributor extends IssFlagCompletionContributor<IssFilePropertyCopyModeElement> {
    public IssFileCopyModeCompletionContributor() {
        super(IssFilePropertyCopyModeElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssFileCopyMode.values();
    }
}

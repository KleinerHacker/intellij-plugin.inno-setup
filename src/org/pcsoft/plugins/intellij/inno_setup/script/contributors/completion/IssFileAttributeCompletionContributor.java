package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyAttributeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyCopyModeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileCopyMode;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFileAttributeCompletionContributor extends IssFlagCompletionContributor<IssFilePropertyAttributeElement> {
    public IssFileAttributeCompletionContributor() {
        super(IssFilePropertyAttributeElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssFileAttribute.values();
    }
}

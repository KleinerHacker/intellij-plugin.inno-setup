package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyAttributeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonIOAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssDirectoryAttributeCompletionContributor extends IssFlagCompletionContributor<IssDirectoryPropertyAttributeElement> {
    public IssDirectoryAttributeCompletionContributor() {
        super(IssDirectoryPropertyAttributeElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssCommonIOAttribute.values();
    }
}

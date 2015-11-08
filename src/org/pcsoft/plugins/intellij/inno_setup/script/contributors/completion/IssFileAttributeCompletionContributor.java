package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyAttributeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonIOAttribute;
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
        return IssCommonIOAttribute.values();
    }
}

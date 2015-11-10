package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion;

import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyAttributeElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonIOAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFlag;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFileAttributeCompletionContributor extends IssAbstractFlagCompletionContributor<IssFilePropertyAttributeElement> {
    public IssFileAttributeCompletionContributor() {
        super(IssFilePropertyAttributeElement.class);
    }

    @Override
    protected IssFlag[] getFlagList() {
        return IssCommonIOAttribute.values();
    }

    @Override
    protected Icon getIcon() {
        return IssIcons.IC_INFO_ATTRIBUTE;
    }
}

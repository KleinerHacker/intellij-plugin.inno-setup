package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.standard.IssPropertyCompressionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCompressionType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypeCompressionValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyCompressionElement> {
    public IssTypeCompressionValueCompletionContributor() {
        super(IssPropertyCompressionElement.class);
    }

    @Override
    protected IssPropertyValue[] getFlagList() {
        return IssCompressionType.values();
    }
}

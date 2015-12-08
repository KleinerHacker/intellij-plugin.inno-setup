package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyCompressionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssCompressionType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssTypeCompressionPropertyValueCompletionContributor extends IssAbstractPropertyValueCompletionContributor<IssPropertyCompressionElement> {
    public IssTypeCompressionPropertyValueCompletionContributor() {
        super(IssPropertyCompressionElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getValueList() {
        return IssCompressionType.values();
    }
}

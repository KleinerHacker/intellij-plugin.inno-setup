package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value.IssAbstractValueCompletionContributor;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyBooleanElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssBooleanTypeCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyBooleanElement> {
    public IssBooleanTypeCompletionContributor() {
        super(IssPropertyBooleanElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        return new IssPropertyValue[] {
                new IssPropertyValue() {
                    @Override
                    public String getId() {
                        return "yes";
                    }

                    @Override
                    public boolean isDeprecated() {
                        return false;
                    }

                    @Override
                    public String getDescriptionKey() {
                        return "";
                    }
                },
                new IssPropertyValue() {
                    @Override
                    public String getId() {
                        return "no";
                    }

                    @Override
                    public boolean isDeprecated() {
                        return false;
                    }

                    @Override
                    public String getDescriptionKey() {
                        return "";
                    }
                }
        };
    }

    @Nullable
    @Override
    public String getTailText(IssPropertyValue propertyValue) {
        return " (" + Boolean.toString(propertyValue.getId().equals("yes")) + ")";
    }
}

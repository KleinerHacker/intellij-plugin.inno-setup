package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyBooleanElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssBooleanValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyBooleanElement> {
    public IssBooleanValueCompletionContributor() {
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
    protected String getTailText(IssPropertyValue propertyValue) {
        return " (" + Boolean.toString(propertyValue.getId().equals("yes")) + ")";
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.type;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value.IssAbstractValueCompletionContributor;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyFontElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssPropertyValue;

import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssFontTypeCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyFontElement> {
    public IssFontTypeCompletionContributor() {
        super(IssPropertyFontElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        final List<IssPropertyValue> list = Stream.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
                .map(item -> new IssPropertyValue() {
                    @Override
                    public String getId() {
                        return item;
                    }

                    @Override
                    public boolean isDeprecated() {
                        return false;
                    }

                    @Override
                    public String getDescriptionKey() {
                        return "";
                    }
                })
                .collect(Collectors.toList());

        return list.toArray(new IssPropertyValue[list.size()]);
    }
}

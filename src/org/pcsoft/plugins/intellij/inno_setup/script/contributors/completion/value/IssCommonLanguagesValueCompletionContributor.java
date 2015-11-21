package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.value;

import com.intellij.openapi.util.IconLoader;
import org.apache.commons.lang.LocaleUtils;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyLanguagesElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyValue;

import javax.swing.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssCommonLanguagesValueCompletionContributor extends IssAbstractValueCompletionContributor<IssPropertyLanguagesElement> {
    public IssCommonLanguagesValueCompletionContributor() {
        super(IssPropertyLanguagesElement.class);
    }

    @NotNull
    @Override
    protected IssPropertyValue[] getFlagList() {
        final List<IssPropertyValue> list = ((List<Locale>)LocaleUtils.availableLocaleList()).stream()
                .map(item -> new IssPropertyValue() {
                    @Override
                    public String getId() {
                        return item.toLanguageTag();
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

    @Override
    protected Icon getIcon(IssPropertyValue propertyValue) {
        final Locale locale = Locale.forLanguageTag(propertyValue.getId());

        Icon icon = IconLoader.findIcon("/icons/lan/" + locale.getCountry().toLowerCase() + ".png");
        if (icon == null) {
            icon = IconLoader.findIcon("/icons/lan/" + locale.getLanguage().toLowerCase() + ".png");
        }
        return icon;
    }
}

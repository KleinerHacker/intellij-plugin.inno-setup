package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Christoph on 30.11.2015.
 */
public enum IssLanguageType {
    Default("Default", "Default.isl", null),
    BrazilianPortuguese("Brazilian Portuguese", "Languages\\BrazilianPortuguese.isl", IconLoader.findIcon("/icons/lan/br.png")),
    Catalan("Catalan", "Languages\\Catalan.isl", IconLoader.findIcon("/icons/lan/es.png")),
    Corsican("Corsican", "Languages\\Corsican.isl", IconLoader.findIcon("/icons/lan/fr.png")),
    Czech("Czech", "Languages\\Czech.isl", IconLoader.findIcon("/icons/lan/cz.png")),
    Danish("Danish", "Languages\\Danish.isl", IconLoader.findIcon("/icons/lan/dk.png")),
    Dutch("Dutch", "Languages\\Dutch.isl", IconLoader.findIcon("/icons/lan/nl.png")),
    Finnish("Finnish", "Languages\\Finnish.isl", IconLoader.findIcon("/icons/lan/fi.png")),
    French("French", "Languages\\French.isl", IconLoader.findIcon("/icons/lan/fr.png")),
    German("German", "Languages\\German.isl", IconLoader.findIcon("/icons/lan/de.png")),
    Greek("Greek", "Languages\\Greek.isl", IconLoader.findIcon("/icons/lan/gr.png")),
    Hebrew("Hebrew", "Languages\\Hebrew.isl", IconLoader.findIcon("/icons/lan/il.png")),
    Hungarian("Hungarian", "Languages\\Hungarian.isl", IconLoader.findIcon("/icons/lan/ua.png")),
    Italian("Italian", "Languages\\Italian.isl", IconLoader.findIcon("/icons/lan/it.png")),
    Japanese("Japanese", "Languages\\Japanese.isl", IconLoader.findIcon("/icons/lan/jp.png")),
    Norwegian("Norwegian", "Languages\\Norwegian.isl", IconLoader.findIcon("/icons/lan/no.png")),
    Polish("Polish", "Languages\\Polish.isl", IconLoader.findIcon("/icons/lan/pl.png")),
    Portuguese("Portuguese", "Languages\\Portuguese.isl", IconLoader.findIcon("/icons/lan/pt.png")),
    Russian("Russian", "Languages\\Russian.isl", IconLoader.findIcon("/icons/lan/ru.png")),
    ScottishGaelic("Scottish Gaelic", "Languages\\ScottishGaelic.isl", IconLoader.findIcon("/icons/lan/ga.png")),
    SerbianCyrillic("Serbian Cyrillic", "Languages\\SerbianCyrillic.isl", IconLoader.findIcon("/icons/lan/rs.png")),
    SerbianLatin("Serbian Latin", "Languages\\SerbianLatin.isl", IconLoader.findIcon("/icons/lan/rs.png")),
    Slovenian("Slovenian", "Languages\\Slovenian.isl", IconLoader.findIcon("/icons/lan/si.png")),
    Spanish("Spanish", "Languages\\Spanish.isl", IconLoader.findIcon("/icons/lan/es.png")),
    Turkish("Turkish", "Languages\\Turkish.isl", IconLoader.findIcon("/icons/lan/tr.png")),
    Ukrainian("Ukrainian", "Languages\\Ukrainian.isl", IconLoader.findIcon("/icons/lan/ua.png")),
    ;

    @Nullable
    public static IssLanguageType findByFile(final String file) {
        for (final IssLanguageType type : values()) {
            if (type.file.equalsIgnoreCase(file))
                return type;
        }

        return null;
    }

    private final String name, file;
    private final Icon flagIcon;

    private IssLanguageType(String name, String file, Icon flagIcon) {
        this.name = name;
        this.file = file;
        this.flagIcon = flagIcon;
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }

    public Icon getFlagIcon() {
        return flagIcon;
    }
}

package org.pcsoft.plugins.intellij.iss.language;

import com.intellij.lang.Language;

/**
 * Created by Christoph on 30.09.2016.
 */
public class IssLanguage extends Language {
    public static final IssLanguage INSTANCE = new IssLanguage();

    public IssLanguage() {
        super("Inno-Setup");
    }
}

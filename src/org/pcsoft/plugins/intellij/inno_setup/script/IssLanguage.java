package org.pcsoft.plugins.intellij.inno_setup.script;

import com.intellij.lang.Language;

/**
 * Created by Christoph on 12.12.2014.
 */
public class IssLanguage extends Language {

    public static final IssLanguage INSTANCE = new IssLanguage();

    private IssLanguage() {
        super("iss");
    }
}

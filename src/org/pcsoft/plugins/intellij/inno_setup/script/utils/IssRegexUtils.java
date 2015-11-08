package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.util.TextRange;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph on 08.11.2015.
 */
public final class IssRegexUtils {
    public static void findInternalConstants(final String text, final Consumer<TextRange> visitor) {
        final Pattern pattern = Pattern.compile("\\{[^\\}]*\\}");
        final Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            visitor.accept(new TextRange(matcher.start(), matcher.end()));
        }
    }

    private IssRegexUtils() {
    }
}

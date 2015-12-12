package org.pcsoft.plugins.intellij.inno_setup.utils;

import java.util.Random;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public final class IssUtils {
    private static final Random RANDOM = new Random();

    public static String generateAppId() {
        final StringBuilder sb = new StringBuilder();

        final int parts = Math.abs(RANDOM.nextInt() % 5) + 5;
        for (int i = 0; i < parts; i++) {
            sb.append(generateRandomString()).append("-");
        }

        return sb.toString().substring(0, sb.length() - 1);
    }

    public static String generateRandomString() {
        final StringBuilder sb = new StringBuilder();

        final int length = Math.abs(RANDOM.nextInt() % 5) + 10;
        for (int i = 0; i < length; i++) {
            sb.append(generateRandomCharacter());
        }

        return sb.toString();
    }

    public static char generateRandomCharacter() {
        final int value = Math.abs(RANDOM.nextInt() % (26 + 10));
        if (value < 26)
            return (char) (RANDOM.nextBoolean() ? 'a' + value : 'A' + value);

        return (char) ('0' + (value - 26));
    }

    private IssUtils() {
    }
}

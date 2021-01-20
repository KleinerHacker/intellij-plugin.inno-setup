package org.pcsoft.plugins.intellij.iss.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

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

    public static boolean checkString(@NotNull String str1, @Nullable String str2, boolean strict) {
        if (strict)
            return str1.equalsIgnoreCase(str2);
        else
            return str2 == null || str1.startsWith(str2);
    }

    private IssUtils() {
    }
}

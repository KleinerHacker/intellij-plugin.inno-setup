package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Christoph on 27.12.2014.
 */
public final class IssAnnotatorUtils {

    @FunctionalInterface
    public static interface ElementValidator<T> {
        boolean validate(T element);
    }

    @FunctionalInterface
    public static interface KeyResolver<T> {
        String resolveKey(T element);
    }

    @FunctionalInterface
    public static interface ElementVisitor<T> {
        void visit(T element, String key);
    }

    public static <T extends PsiElement> void findDoubleValues(Collection<T> elements, final KeyResolver<T> keyResolver, final ElementVisitor<T> elementVisitor) {
        findDoubleValues(elements, o -> true, keyResolver, elementVisitor);
    }

    public static <T extends PsiElement> void findDoubleValues(Collection<T> elements, final ElementValidator<T> elementValidator, final KeyResolver<T> keyResolver, final ElementVisitor<T> elementVisitor) {
        final Map<String, List<T>> map = new HashMap<>();
        for (final T element : elements) {
            if (!elementValidator.validate(element))
                continue;

            if (!map.containsKey(keyResolver.resolveKey(element))) {
                map.put(keyResolver.resolveKey(element), new ArrayList<>());
            }
            map.get(keyResolver.resolveKey(element)).add(element);
        }

        map.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry -> {
                    for (final T element : entry.getValue()) {
                        elementVisitor.visit(element, entry.getKey());
                    }
                });
    }

    private IssAnnotatorUtils() {
    }
}

package org.pcsoft.plugins.intellij.inno_setup.script.types;

import org.jetbrains.annotations.NotNull;
import sun.misc.CompoundEnumeration;

import java.util.Enumeration;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Created by Christoph on 08.11.2015.
 */
public class MultiResourceBundle extends ResourceBundle {
    private final ResourceBundle[] resourceBundles;

    public MultiResourceBundle(@NotNull ResourceBundle... resourceBundles) {
        this.resourceBundles = resourceBundles;
    }

    @NotNull
    public ResourceBundle[] getResourceBundles() {
        return resourceBundles;
    }

    @Override
    protected Object handleGetObject(String key) {
        final ResourceBundle resourceBundle = Stream.of(resourceBundles)
                .filter(rb -> rb.containsKey(key))
                .findFirst().orElse(null);
        return resourceBundle == null ? null : resourceBundle.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        final Enumeration<String>[] enumerations = new Enumeration[resourceBundles.length];
        for (int i=0; i<resourceBundles.length; i++) {
            enumerations[i] = resourceBundles[i].getKeys();
        }

        return new CompoundEnumeration<>(enumerations);
    }
}

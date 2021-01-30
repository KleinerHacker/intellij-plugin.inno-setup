package org.pcsoft.plugins.intellij.iss.core.language.type.base;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 02.10.2016.
 */
public interface TypeBase {
    @NotNull
    String getName();

    boolean isDeprecated();
}

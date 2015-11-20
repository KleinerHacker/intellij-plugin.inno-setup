package org.pcsoft.plugins.intellij.inno_setup.script.types;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 27.12.2014.
 */
public interface IssDefinablePropertyIdentifier extends IssPropertyIdentifier {

    @NotNull
    IssValueType getValueType();

}

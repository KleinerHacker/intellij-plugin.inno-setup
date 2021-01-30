package org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation;

import org.pcsoft.plugins.intellij.iss.core.language.type.base.PropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a {@link PropertyType} as Info Property element.<br/>
 * With this marker its value will be used as info text after property key (see {@link IsKeyProperty}) in tree view.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IsInfoProperty {
}

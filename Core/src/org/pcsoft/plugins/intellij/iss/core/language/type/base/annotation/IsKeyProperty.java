package org.pcsoft.plugins.intellij.iss.core.language.type.base.annotation;

import org.pcsoft.plugins.intellij.iss.core.language.type.base.PropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a {@link PropertyType} as Key Property element.<br/>
 * With this marker its value will be used as node text in tree view.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IsKeyProperty {
}

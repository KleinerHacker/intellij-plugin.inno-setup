package org.pcsoft.plugins.intellij.iss.language.type.base.annotation;

import org.pcsoft.plugins.intellij.iss.language.type.base.SectionProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a {@link SectionProperty} as Key Property element.<br/>
 * With this marker its value will be used as node text in tree view.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IsKeyProperty {
}

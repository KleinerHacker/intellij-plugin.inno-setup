package org.pcsoft.plugins.intellij.iss.language.type.base.annotation;

import org.pcsoft.plugins.intellij.iss.language.type.SectionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a property to reference to a section line in given section type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ReferenceTo {
    SectionType value();
}

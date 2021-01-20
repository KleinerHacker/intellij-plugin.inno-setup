package org.pcsoft.plugins.intellij.iss.language.type;

/**
 * Describe the {@link SectionType} variant
 */
public enum SectionTypeVariant {
    /**
     * Default variant, means a=b
     */
    Default,
    /**
     * Line based variant, means a:b;c:d
     */
    LineBased,
    /**
     * Code variant (<b>must be the last part in code</b>), means Delphi script section
     */
    Code
}

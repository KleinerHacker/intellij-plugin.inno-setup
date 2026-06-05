package org.pcsoft.intellij.plugin.inno_setup

/**
 * Marks a declaration that is intentionally excluded from code-coverage metrics —
 * typically defensive code containing a provably-unreachable branch.
 *
 * Honored by Kover via the `annotatedBy` filter (see `build.gradle.kts`) and, by
 * convention, by JaCoCo's built-in `*Generated*` filter. The annotation has
 * `RUNTIME` retention so it is visible to bytecode-based coverage tools.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
annotation class Generated

/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

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
/**
 * Provides Inno Setup plugin behavior for the IntelliJ Platform.
 */
annotation class Generated

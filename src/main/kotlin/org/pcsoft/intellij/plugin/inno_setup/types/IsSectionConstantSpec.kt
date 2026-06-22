/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Groups built-in constants by their semantic source.
 */
enum class IsSectionConstantCategorySpec {
    /** Directory constants such as application or installation paths. */
    @JsonProperty("directory")
    DIRECTORY,

    /** Windows shell-folder constants. */
    @JsonProperty("shell_folder")
    SHELL_FOLDER,

    /** Constants whose value is resolved automatically by Inno Setup. */
    @JsonProperty("auto")
    AUTO,

    /** Special constants that do not fit the other categories. */
    @JsonProperty("special")
    SPECIAL,

    /** Constants that require parameters inside their braces. */
    @JsonProperty("parameterized")
    PARAMETERIZED
}

/**
 * Describes a built-in Inno Setup constant.
 *
 * @property name Constant name without surrounding braces.
 * @property description HTML-capable description loaded from the constant specification.
 * @property deprecated Targets for which the constant is marked as deprecated.
 * @property type Semantic category used for presentation and grouping.
 * @property parameterized Whether the constant requires a parameter.
 * @property syntax Optional syntax for parameterized constants.
 * @property since First Inno Setup version that supports the constant, or `null` when unknown.
 * @property until Last Inno Setup version that supports the constant, or `null` when it is still valid.
 */
data class IsSectionBuiltinConstantSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val name: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val description: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val deprecated: Set<IsSectionSpecTarget> = emptySet(),
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val type: IsSectionConstantCategorySpec = IsSectionConstantCategorySpec.SPECIAL,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val parameterized: Boolean = false,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val syntax: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val since: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val until: String? = null
)

/**
 * Root model for the bundled Inno Setup constants specification.
 *
 * @property constants All built-in constants known to the plugin.
 */
data class IsSectionConstantSpec(val constants: List<IsSectionBuiltinConstantSpec>)
